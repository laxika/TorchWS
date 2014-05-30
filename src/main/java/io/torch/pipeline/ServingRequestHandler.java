package io.torch.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.torch.http.request.RequestMethod;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.request.file.FileRequestProcessor;
import io.torch.http.request.webpage.WebpageRequestProcessor;
import io.torch.http.response.TorchHttpResponse;
import io.torch.route.Route;
import io.torch.route.RouteManager;
import io.torch.util.ChannelVariable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServingRequestHandler extends ChannelInboundHandlerAdapter {

    private final WebpageRequestProcessor webpageRequestProcessor;
    private final FileRequestProcessor fileRequestProcessor;

    public ServingRequestHandler() {
        webpageRequestProcessor = new WebpageRequestProcessor();
        fileRequestProcessor = new FileRequestProcessor();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

        Route route = calculateRouteFromRequest(ctx, request);

        TorchHttpResponse response = new TorchHttpResponse();
        TorchHttpRequest torchreq = new TorchHttpRequest(request, route);

        if (route != null) {
            webpageRequestProcessor.processRequest(ctx, torchreq, response);
        } else {
            fileRequestProcessor.processRequest(ctx, torchreq, response);
        }

        request.release();
    }

    private Route calculateRouteFromRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        RouteManager routeManager = (RouteManager) ctx.channel().attr(ChannelVariable.ROUTE_MANAGER.getVariableKey()).get();

        return routeManager.calculateRouteByUrl(request.getUri(), RequestMethod.getMethodByNettyMethod(request.getMethod()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logger.getLogger(ServingRequestHandler.class.getName()).log(Level.WARNING, null, cause);
        
        ctx.close();
    }
}
