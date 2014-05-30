package io.torch.http.request;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.torch.http.response.TorchHttpResponse;

public abstract class RequestProcessor {

    public abstract void processRequest(ChannelHandlerContext ctx, TorchHttpRequest torchreq, TorchHttpResponse response);

    protected void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status, TorchHttpRequest torchreq) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("404 Not found!", CharsetUtil.UTF_8));

        handleKeepAliveHeader(torchreq, fullresponse);

        fullresponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

        ctx.write(fullresponse);
        ctx.flush();
    }

    protected void handleKeepAliveHeader(TorchHttpRequest torchreq, FullHttpResponse fullresponse) {
        if (torchreq.isKeepAlive()) {
            // Add 'Content-Length' header only for a keep-alive connection.
            fullresponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fullresponse.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            fullresponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
    }
}
