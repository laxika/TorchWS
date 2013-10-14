package io.torch.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRequestValidator extends WebResponseHandler {

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
            sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST, request);
            return;
        }

        ctx.fireChannelRead(msg);
    }
}
