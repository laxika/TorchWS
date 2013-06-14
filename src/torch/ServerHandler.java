package torch;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import io.netty.util.CharsetUtil;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.router.RouteManager;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final RouteManager routes;

    public ServerHandler(RouteManager container) {
        this.routes = container;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            
            //Handle the message
            TorchHttpResponse response = new TorchHttpResponse();
            TorchHttpRequest torchreq = new TorchHttpRequest(request);

            if (routes.getRouteTarget(request.getUri()) != null) {
                routes.getRouteTarget(request.getUri()).handle(torchreq, response);
            } else {
                //404
            }

            writeResponse(ctx, response, HttpHeaders.isKeepAlive(request));
        }
    }
    
    private void writeResponse(ChannelHandlerContext ctx, TorchHttpResponse response, boolean keepAlive) {
            FullHttpResponse fullresponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));

            fullresponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                fullresponse.headers().set(CONTENT_LENGTH, fullresponse.content().readableBytes());
                // Add keep alive header as per:
                // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                fullresponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            // Write the response.
            ctx.nextOutboundMessageBuffer().add(fullresponse);

            // Close the non-keep-alive connection after the write operation is done.
            if (!keepAlive) {
                ctx.flush().addListener(ChannelFutureListener.CLOSE);
            }
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
}
