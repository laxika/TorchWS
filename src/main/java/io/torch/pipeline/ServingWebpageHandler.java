package io.torch.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import io.torch.controller.WebPage;
import io.torch.cookie.CookieVariable;
import io.torch.http.request.RequestMethod;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.http.response.status.ClientErrorResponseStatus;
import io.torch.http.response.status.ServerErrorResponseStatus;
import io.torch.route.Route;
import io.torch.route.RouteManager;
import io.torch.session.Session;
import io.torch.session.SessionManager;
import io.torch.template.TemplateManager;
import io.torch.template.Templateable;
import io.torch.util.ChannelVariable;
import java.util.Map;

public class ServingWebpageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

        Route route = calculateRouteFromRequest(ctx, request);

        //Check that we the target of the route
        if (route != null) {
            TorchHttpResponse response = new TorchHttpResponse();
            TorchHttpRequest torchreq = new TorchHttpRequest(request, route);

            //Calculating the actual session, if no session data recived, start a new one
            CookieVariable sessionCookie = torchreq.getCookieData().getCookie("SESSID");
            
            SessionManager sessionManager = (SessionManager) ctx.channel().attr(ChannelVariable.SESSION_MANAGER.getVariableKey()).get();

            Session session;
            if (sessionCookie != null && sessionManager.getSession(sessionCookie.getValue()) != null) {
                session = sessionManager.getSession(sessionCookie.getValue());
            } else {
                session = sessionManager.startNewSession();
                response.getCookieData().putCookie(new CookieVariable("SESSID", session.getSessionId(), "/"));
            }

            //Instantiate a new WebPage object and handle the request
            WebPage webpage = route.getTarget().newInstance();

            webpage.handle(torchreq, response, session);

            TemplateManager templateManager = (TemplateManager) ctx.channel().attr(ChannelVariable.TEMPLATE_MANAGER.getVariableKey()).get();
            FullHttpResponse fullresponse;
            if (response.getStatus() instanceof ServerErrorResponseStatus || response.getStatus() instanceof ClientErrorResponseStatus) {
                if (templateManager.isTemplateExist("error/" + response.getStatus().getStatusCode() + ".tpl")) {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer(templateManager.processTemplate("error/" + response.getStatus().getStatusCode() + ".tpl", null), CharsetUtil.UTF_8));
                } else {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer("Error " + response.getStatus().getStatusCode(), CharsetUtil.UTF_8));
                }
            } else {
                //Generate the template
                if (webpage instanceof Templateable && ((Templateable) webpage).getTemplate() != null) {
                    Templateable templateableWebpage = (Templateable) webpage;

                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer(templateManager.processTemplate(templateableWebpage.getTemplate(), templateableWebpage.getTemplateRoot()), CharsetUtil.UTF_8));
                } else {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));
                }
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
                DefaultCookie realCookie = new DefaultCookie(cookie.getName(), cookie.getValue());

                if (cookie.getPath() != null) {
                    realCookie.setPath(cookie.getPath());
                }
                fullresponse.headers().set(Names.SET_COOKIE, ServerCookieEncoder.encode(realCookie));
            }

            //Setting the headers
            for (Object pairs : response.getHeaderData()) {
                Map.Entry<String, String> obj = (Map.Entry<String, String>) pairs;

                fullresponse.headers().add(obj.getKey(), obj.getValue());
            }

            // Write the response.
            if (!HttpHeaders.isKeepAlive(request)) {
                ctx.write(fullresponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.write(fullresponse);
            }
            ctx.flush();
            request.release();
        } else {
            //Forward the message to the file handler to server a static asset
            ctx.fireChannelRead(msg);
        }
    }

    private Route calculateRouteFromRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        RouteManager routeManager = (RouteManager) ctx.channel().attr(ChannelVariable.ROUTE_MANAGER.getVariableKey()).get();

        return routeManager.calculateRouteByUrl(request.getUri(), RequestMethod.getMethodByNettyMethod(request.getMethod()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
