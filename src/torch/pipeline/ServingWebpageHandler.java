package torch.pipeline;

import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import java.io.StringWriter;
import java.util.Map;
import torch.controller.WebPage;
import torch.cookie.CookieVariable;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.http.request.RequestMethod;
import torch.route.Route;
import torch.route.RouteManager;
import torch.session.Session;
import torch.session.SessionManager;
import torch.template.TemplateManager;
import torch.template.Templateable;
import torch.util.ChannelVariable;

public class ServingWebpageHandler extends ChannelInboundHandlerAdapter {

    protected static SessionManager sessionManager = new SessionManager();
    protected static TemplateManager templateManager = new TemplateManager();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

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
            ctx.flush();
        } else {
            //Forward the message to the file handler to server a static asset
            ctx.fireChannelRead(msg);
        }
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
        //cause.printStackTrace(System.out); -- enable this only in dev environment!
        ctx.close();
    }
}
