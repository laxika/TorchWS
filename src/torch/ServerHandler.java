package torch;

import freemarker.template.Template;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Map;
import torch.cookie.Cookie;
import torch.handler.WebPage;
import torch.http.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.route.Route;
import torch.route.RouteManager;
import torch.session.Session;
import torch.session.SessionManager;
import torch.template.TemplateManager;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private final RouteManager routes;
    protected static SessionManager sessionManager = new SessionManager();
    protected static TemplateManager templateManager = new TemplateManager();

    public ServerHandler(RouteManager container) {
        routes = container;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            Route target = routes.calculateRouteByUrl(request.getUri(), RequestMethod.getMethodByNettyMethod(request.getMethod()));

            //Check that we the target of the route
            if (target != null) {            //Handle the message
                TorchHttpResponse response = new TorchHttpResponse();
                TorchHttpRequest torchreq = new TorchHttpRequest(request, target);

                Session session = sessionManager.getSession(torchreq.getCookieData().getCookie("SESSID").getValue());

                //New session
                if (session == null) {
                    session = sessionManager.startNewSession();
                    response.getCookieData().addCookie(new Cookie("SESSID", session.getSessionId()));
                }

                //Instantiate a new WebPage object and handle the request
                WebPage webpage = (WebPage) target.getTarget().getConstructor().newInstance();
                webpage.handle(torchreq, response, session);

                FullHttpResponse fullresponse;
                if (webpage.getTemplate() == null) {
                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));
                } else {
                    Template temp = templateManager.getTemplate(webpage.getTemplate());

                    StringWriter templateText = new StringWriter();

                    temp.process(webpage.getTemplateRoot(), templateText);

                    fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(templateText.toString(), CharsetUtil.UTF_8));
                }

                fullresponse.headers().set(CONTENT_TYPE, response.getContentType());

                //Setting the new cookies
                for (Object pairs : response.getCookieData()) {
                    Cookie obj = ((Map.Entry<String, Cookie>) pairs).getValue();

                    fullresponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(obj.getName(), obj.getValue()));
                }

                // Write the response.
                ctx.write(fullresponse);
            } else {
                //Send back not found 404
                sendErrorResponse(ctx, HttpResponseStatus.NOT_FOUND);
            }
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("404 Not found!", CharsetUtil.UTF_8));

        fullresponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        ctx.write(fullresponse);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
        ctx.close();
    }
}
