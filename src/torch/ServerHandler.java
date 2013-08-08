package torch;

import torch.util.ChannelVariable;
import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.ServerCookieEncoder;
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
import torch.cookie.CookieVariable;
import torch.handler.WebPage;
import torch.http.request.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.route.Route;
import torch.route.RouteManager;
import torch.session.Session;
import torch.session.SessionManager;
import torch.template.TemplateManager;
import torch.template.Templateable;

public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected static SessionManager sessionManager = new SessionManager();
    protected static TemplateManager templateManager = new TemplateManager();
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        //Check that the request was successfull
        if (validateRequest(request)) {
            sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        Route target = calculateRouteFromRequest(ctx, request);

        //Check that we the target of the route
        if (target != null) {
            TorchHttpResponse response = new TorchHttpResponse();
            TorchHttpRequest torchreq = new TorchHttpRequest(request, target);

            Session session = sessionManager.getSession(torchreq.getCookieData().getCookie("SESSID").getValue());

            //New session
            if (session == null) {
                session = sessionManager.startNewSession();
                response.getCookieData().addCookie(new CookieVariable("SESSID", session.getSessionId()));
            }

            //Instantiate a new WebPage object and handle the request
            WebPage webpage = (WebPage) target.getTarget().getConstructor().newInstance();
            webpage.handle(torchreq, response, session);

            FullHttpResponse fullresponse;
            if (webpage instanceof Templateable) {
                Template temp = templateManager.getTemplate(((Templateable) webpage).getTemplate());

                StringWriter templateText = new StringWriter();

                temp.process(((Templateable) webpage).getTemplateRoot(), templateText);

                fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(templateText.toString(), CharsetUtil.UTF_8));
            } else {
                fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));
            }

            if (HttpHeaders.isKeepAlive(request)) {
                // Add 'Content-Length' header only for a keep-alive connection.
                fullresponse.headers().set(Names.CONTENT_LENGTH, fullresponse.content().readableBytes());
                // Add keep alive header as per:
                // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                fullresponse.headers().set(Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            fullresponse.headers().set(Names.CONTENT_TYPE, response.getContentType());

            //Setting the new cookies
            for (CookieVariable cookie : response.getCookieData()) {
                fullresponse.headers().add(Names.SET_COOKIE, ServerCookieEncoder.encode(cookie.getName(), cookie.getValue()));
            }
            
            //Setting the headers
            for (Object pairs : response.getHeaderData()) {
                Map.Entry<String, String> obj = (Map.Entry<String, String>) pairs;

                fullresponse.headers().add(obj.getKey(), obj.getValue());
            }

            // Write the response.
            ctx.write(fullresponse);
        } else {
            File file = new File("public" + request.getUri());

            if (file.exists() && file.isFile()) {
                // Cache Validation
                String ifModifiedSince = request.headers().get(Names.IF_MODIFIED_SINCE);
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
                    response.headers().set(Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                }

                // Write the initial line and the header.
                ctx.write(response);

                ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());

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
        ctx.flush();
    }

    private boolean validateRequest(FullHttpRequest request) {
        return !request.getDecoderResult().isSuccess();
    }

    private Route calculateRouteFromRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        RouteManager routeManager = (RouteManager) ctx.channel().attr(ChannelVariable.ROUTE_MANAGER.getVariableKey()).get();

        return routeManager.calculateRouteByUrl(request.getUri(), RequestMethod.getMethodByNettyMethod(request.getMethod()));
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("404 Not found!", CharsetUtil.UTF_8));

        fullresponse.headers().set(Names.CONTENT_TYPE, "text/html; charset=UTF-8");

        ctx.write(fullresponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
        ctx.close();
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a
     * "304 Not Modified"
     *
     * @param ctx Context
     */
    private static void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(Names.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response HTTP response
     * @param fileToCache file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(Names.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(Names.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}
