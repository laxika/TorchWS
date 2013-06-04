package torch;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import javax.activation.MimetypesFileTypeMap;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private HttpRequest request;
    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            if (true) {
                //Has a route to a class what handle it
                buf.append("Route!");
                boolean keepAlive = isKeepAlive(request);
                // Build the response object.
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, OK,
                        Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

                response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

                if (keepAlive) {
                    // Add 'Content-Length' header only for a keep-alive connection.
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                    // Add keep alive header as per:
                    // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                }

                // Encode the cookie.
                String cookieString = request.headers().get(COOKIE);
                if (cookieString != null) {
                    Set<Cookie> cookies = CookieDecoder.decode(cookieString);
                    if (!cookies.isEmpty()) {
                        // Reset the cookies if necessary.
                        for (Cookie cookie : cookies) {
                            response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
                        }
                    }
                } else {
                    // Browser sent no cookie.  Add some.
                    response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
                    response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
                }

                // Write the response.
                ctx.nextOutboundMessageBuffer().add(response);

                // Close the non-keep-alive connection after the write operation is done.
                if (!keepAlive) {
                    ctx.flush().addListener(ChannelFutureListener.CLOSE);
                }
            } else {
                File file = new File("public/" + request.getUri());
                file.mkdirs();
                RandomAccessFile raf2 = new RandomAccessFile(file, "rw");
                raf2.writeInt(12);
                raf2.close();

                if (file.exists()) {
                    RandomAccessFile raf = new RandomAccessFile(file, "r");

                    long fileLength = raf.length();
                    HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

                    setContentLength(response, fileLength);

                    //Set mime type
                    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
                    response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));

                    setDateAndCacheHeaders(response, file);
                    if (isKeepAlive(request)) {
                        response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    }

                    // Write the initial line and the header.
                    ctx.write(response);

                    // Write the content.
                    ChannelFuture writeFuture = ctx.write(new ChunkedFile(raf, 0, fileLength, 8192));

                    // Decide whether to close the connection or not.
                    if (!isKeepAlive(request)) {
                        // Close the connection when the whole content is written out.
                        writeFuture.addListener(ChannelFutureListener.CLOSE);
                    }
                } else {
                    sendError(ctx, NOT_FOUND);
                }
                return;
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.getDecoderResult();
        if (result.isSuccess()) {
            return;
        }

        buf.append(".. WITH DECODER FAILURE: ");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void endMessageReceived(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     * HTTP response
     * @param fileToCache
     * file to extract content type
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
}
