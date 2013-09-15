package io.torch.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
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
import io.netty.util.CharsetUtil;
import io.torch.file.MimeTypeDetector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.io.FilenameUtils;

public class ServingFileHandler extends ChannelInboundHandlerAdapter {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private static final MimeTypeDetector mimeDetector= new MimeTypeDetector();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

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
                    request.release();
                    return;
                }
            }

            RandomAccessFile raf;
            try {
                raf = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException fnfe) {
                sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND, request);
                request.release();
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
            sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND, request);
        }

        ctx.flush();
        request.release();
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status, FullHttpRequest request) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("404 Not found!", CharsetUtil.UTF_8));


        if (HttpHeaders.isKeepAlive(request)) {
            // Add 'Content-Length' header only for a keep-alive connection.
            fullresponse.headers().set(Names.CONTENT_LENGTH, fullresponse.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            fullresponse.headers().set(Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        fullresponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

        ctx.write(fullresponse);
        ctx.flush();
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
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));
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
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) throws IOException {
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,mimeDetector.getMimeByExtension(FilenameUtils.getExtension(file.getName())));
    }
}
