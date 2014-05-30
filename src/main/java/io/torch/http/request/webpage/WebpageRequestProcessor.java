package io.torch.http.request.webpage;

import freemarker.template.TemplateException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import io.torch.controller.Validable;
import io.torch.controller.WebPage;
import io.torch.cookie.CookieVariable;
import io.torch.http.request.RequestProcessor;
import io.torch.http.request.TorchHttpRequest;
import io.torch.http.response.TorchHttpResponse;
import io.torch.http.response.status.ClientErrorResponseStatus;
import io.torch.http.response.status.ServerErrorResponseStatus;
import io.torch.session.Session;
import io.torch.session.SessionManager;
import io.torch.template.TemplateManager;
import io.torch.template.TemplateRootLocator;
import io.torch.template.Templateable;
import io.torch.util.ChannelVariable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebpageRequestProcessor extends RequestProcessor {

    private final TemplateRootLocator templateRootLocator;

    public WebpageRequestProcessor() {
        templateRootLocator = new TemplateRootLocator();
    }

    @Override
    public void processRequest(ChannelHandlerContext ctx, TorchHttpRequest torchreq, TorchHttpResponse response) {
        try {
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
            WebPage webpage = torchreq.getRoute().getTarget().newInstance();

            if (webpage instanceof Validable) {
                if (!((Validable) webpage).validate(torchreq, response, session)) {
                    sendErrorResponse(ctx, HttpResponseStatus.BAD_REQUEST, torchreq);
                    return;
                }
            }

            synchronized (session) {
                webpage.handle(torchreq, response, session);
            }

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
                if (webpage.getClass().isAnnotationPresent(Templateable.class)) {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer(templateManager.processTemplate(webpage.getClass().getAnnotation(Templateable.class).path(), templateRootLocator.locateTemplateRoot(webpage)), CharsetUtil.UTF_8));
                } else {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus().getStatusCode()), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));
                }
            }

            handleKeepAliveHeader(torchreq, fullresponse);

            fullresponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, response.getContentType());

            //Setting the new cookies
            for (CookieVariable cookie : response.getCookieData()) {
                DefaultCookie realCookie = new DefaultCookie(cookie.getName(), cookie.getValue());

                if (cookie.getPath() != null) {
                    realCookie.setPath(cookie.getPath());
                }
                fullresponse.headers().set(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(realCookie));
            }

            //Setting the headers
            for (Object pairs : response.getHeaderData()) {
                Map.Entry<String, String> obj = (Map.Entry<String, String>) pairs;

                fullresponse.headers().add(obj.getKey(), obj.getValue());
            }

            // Write the response.
            if (!torchreq.isKeepAlive()) {
                ctx.write(fullresponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                ctx.write(fullresponse);
            }
            ctx.flush();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | TemplateException ex) {
            Logger.getLogger(WebpageRequestProcessor.class.getName()).log(Level.SEVERE, null, ex);

            // If something went wrong we're better off just denying it
            sendErrorResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, torchreq);
        }
    }

}
