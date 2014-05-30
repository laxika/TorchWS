package io.torch.http.request.file;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.torch.file.MimeTypeDetector;
import io.torch.http.header.HeaderVariable;
import io.torch.http.request.RequestProcessor;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

public class FileRequestProcessor extends RequestProcessor {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private static final MimeTypeDetector mimeDetector = new MimeTypeDetector();

    private static final File PUBLIC_FOLDER = new File("public");
    private static final String PUBLIC_PATH = PUBLIC_FOLDER.getAbsolutePath();
    
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
    
    static {
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
    }

    @Override
    public void processRequest(ChannelHandlerContext ctx, TorchHttpRequest torchRequest, TorchHttpResponse torchResponse) {
        File file = new File(PUBLIC_FOLDER, torchRequest.getUri());

        if (!this.validatePath(file) || !file.exists() || !file.isFile()) {
            sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND, torchRequest);

            return;
        }
        
        try {
            // Cache Validation
            HeaderVariable ifModifiedSince = torchRequest.getHeaderData().getHeader(HttpHeaders.Names.IF_MODIFIED_SINCE);
            
            if (ifModifiedSince != null) {
                try {
                    Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince.getValue());

                    // Only compare up to the second because the datetime format we send to the client
                    // does not have milliseconds
                    long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                    long fileLastModifiedSeconds = file.lastModified() / 1000;
                    if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                        sendNotModified(ctx);
                        return;
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(FileRequestProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            RandomAccessFile raf;
            try {
                raf = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException fnfe) {
                sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND, torchRequest);
                return;
            }
            long fileLength = raf.length();

            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders.setContentLength(response, fileLength);
            setContentTypeHeader(response, file);
            setDateAndCacheHeaders(response, file);

            if (torchRequest.isKeepAlive()) {
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            // Write the initial line and the header.
            ctx.write(response);

            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());

            // Write the end marker
            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            // Decide whether to close the connection or not.
            if (!torchRequest.isKeepAlive()) {
                // Close the connection when the whole content is written out.
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileRequestProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(new Date(System.currentTimeMillis())));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response HTTP response
     * @param fileToCache file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(new Date(System.currentTimeMillis())));
        response.headers().set(HttpHeaders.Names.EXPIRES, dateFormatter.format(new Date(System.currentTimeMillis() + (HTTP_CACHE_SECONDS * 1000))));
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
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimeDetector.getMimeByExtension(FilenameUtils.getExtension(file.getName())));
    }

    private boolean validatePath(File file) {
        try {
            String requestPath = file.getCanonicalPath();

            if (!requestPath.startsWith(PUBLIC_PATH)) {
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(FileRequestProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

}
