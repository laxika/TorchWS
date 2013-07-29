package torch;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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
import java.util.Map;
import torch.cookie.Cookie;
import torch.cookie.CookieStorage;
import torch.http.RequestMethod;
import torch.http.TorchHttpRequest;
import torch.http.TorchHttpResponse;
import torch.route.Route;
import torch.route.RouteManager;
import torch.session.Session;
import torch.session.SessionManager;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private final RouteManager routes;
    protected static SessionManager sessionManager = new SessionManager();

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

            CookieStorage cookieStorage = new CookieStorage(request.headers().get(COOKIE));

            //Handle the message
            TorchHttpResponse response = new TorchHttpResponse(cookieStorage);
            TorchHttpRequest torchreq = new TorchHttpRequest(request, target);

            Session session = sessionManager.getSession(cookieStorage.getCookie("SESSID").getValue());

            //New session
            if (session == null) {
                session = sessionManager.startNewSession();
                response.getNewCookieData().addCookie(new Cookie("SESSID", session.getSessionId()));
            }

            if (target != null) {
                target.getTarget().handle(torchreq, response, session);
            } else {
                response.appendContent("404 Not Found!");
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            }

            FullHttpResponse fullresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.getStatus(), Unpooled.copiedBuffer(response.getContent(), CharsetUtil.UTF_8));

            fullresponse.headers().set(CONTENT_TYPE, response.getContentType());

            //Setting the new cookies
            for (Object pairs : response.getNewCookieData()) {
                Cookie obj = ((Map.Entry<String, Cookie>) pairs).getValue();

                fullresponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(obj.getName(), obj.getValue()));
            }

            // Write the response.
            ctx.write(fullresponse);
        }
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
