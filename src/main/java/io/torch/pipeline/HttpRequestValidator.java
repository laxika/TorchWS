package io.torch.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpRequestValidator extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //Check thet it's a http request if not close the channel
        if (!(msg instanceof FullHttpRequest)) {
            ctx.close();
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        //Check that the request is successfull
        if (!request.getDecoderResult().isSuccess()) {
            //TODO: create a general method for sending errors from all handlers
            FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer("400 Bad Request!", CharsetUtil.UTF_8));

            if (HttpHeaders.isKeepAlive(request)) {
                fullresponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fullresponse.content().readableBytes());
                fullresponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            fullresponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

            ctx.write(fullresponse);
            ctx.flush();
            request.release();
            return;
        }
        
        ctx.fireChannelRead(msg);
    }
}
