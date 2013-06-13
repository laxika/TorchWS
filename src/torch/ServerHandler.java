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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import torch.handler.WebPageHandler;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final HashMap<String, WebPageHandler> container;

    public ServerHandler(HashMap<String, WebPageHandler> container) {
        this.container = container;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            //Handle the message
            TorchHttpResponse response = new TorchHttpResponse();
            TorchHttpRequest torchreq = new TorchHttpRequest(request);

            if (container.containsKey(request.getUri())) {
                container.get(request.getUri()).handle(torchreq, response);
            } else {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            }

            writeResponse(ctx, response, HttpHeaders.isKeepAlive(request));
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, TorchHttpResponse response, boolean keepAlive) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));

        fullresponse.headers().set(CONTENT_TYPE, response.getContentType());

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            fullresponse.headers().set(CONTENT_LENGTH, fullresponse.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            fullresponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        //Setting the new cookies
        for (Map.Entry<String, String> pairs : response.getNewCookieData().entrySet()) {
            fullresponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(pairs.getKey(), pairs.getValue()));
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
