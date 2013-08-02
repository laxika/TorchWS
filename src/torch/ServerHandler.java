package torch;

import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.activation.MimetypesFileTypeMap;
import torch.cookie.Cookie;
import torch.handler.WebPage;
import torch.http.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.route.Route;
import torch.route.RouteManager;
import torch.session.Session;
import torch.session.SessionManager;
import torch.template.TemplateManager;

public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final RouteManager routes;
    protected static SessionManager sessionManager = new SessionManager();
    protected static TemplateManager templateManager = new TemplateManager();
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private final boolean useSendFile;

    public ServerHandler(RouteManager container, boolean useSendFile) {
        this.routes = container;
        this.useSendFile = useSendFile;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (!request.getDecoderResult().isSuccess()) {
            sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        Route target = routes.calculateRouteByUrl(request.getUri(), RequestMethod.getMethodByNettyMethod(request.getMethod()));

        //Check that we the target of the route
        if (target != null) {            //Handle the message
            TorchHttpResponse response = new TorchHttpResponse();
            TorchHttpRequest torchreq = new TorchHttpRequest(request, target);

            Session session = sessionManager.getSession(torchreq.getCookieData().getCookie("SESSID").getValue());

            //New session
            if (session == null) {
                session = sessionManager.startNewSession();
                response.getCookieData().addCookie(new Cookie("SESSID", session.getSessionId()));
            }

            //Instantiate a new WebPage object and handle the request
            WebPage webpage = (WebPage) target.getTarget().getConstructor().newInstance();
            webpage.handle(torchreq, response, session);

            FullHttpResponse fullresponse;
            if (webpage.getTemplate() == null) {
                fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));
            } else {
                Template temp = templateManager.getTemplate(webpage.getTemplate());

                StringWriter templateText = new StringWriter();

                temp.process(webpage.getTemplateRoot(), templateText);

                fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(templateText.toString(), CharsetUtil.UTF_8));
            }

            fullresponse.headers().set(CONTENT_TYPE, response.getContentType());

            //Setting the new cookies
            for (Object pairs : response.getCookieData()) {
                Cookie obj = ((Map.Entry<String, Cookie>) pairs).getValue();

                fullresponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(obj.getName(), obj.getValue()));
            }

            // Write the response.
            ctx.write(fullresponse);
        } else {
            File file = new File("public" + request.getUri());
            
            System.out.println("FILE REQUEST: "+file.getPath());

            if (file.exists() && file.isFile()) {
                // Cache Validation
                String ifModifiedSince = request.headers().get(IF_MODIFIED_SINCE);
                if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
                    Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

                    // Only compare up to the second because the datetime format we send to the client
                    // does not have milliseconds
                    long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                    long fileLastModifiedSeconds = file.lastModified() / 1000;
                    if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                        sendNotModified(ctx);
                        return;
                    }
                }
                
                RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeaders.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        setDateAndCacheHeaders(response, file);
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        ctx.write(response);

        // Write the content.
        ChannelFuture sendFileFuture;
        if (useSendFile) {
            sendFileFuture =
                    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
        } else {
            sendFileFuture =
                    ctx.write(new ChunkedFile(raf, 0, fileLength, 8192), ctx.newProgressivePromise());
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.err.println("Transfer complete.");
            }
        });

        // Write the end marker
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not.
        if (!HttpHeaders.isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
            } else {
                //Send back not found 404
                sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND);
            }
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("404 Not found!", CharsetUtil.UTF_8));

        fullresponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        ctx.write(fullresponse);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
        ctx.close();
    }
    
    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    private static void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }/**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param fileToCache
     *            file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}
