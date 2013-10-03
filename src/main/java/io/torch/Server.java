package io.torch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.torch.route.RouteManager;
import io.torch.session.DefaultSessionManager;
import io.torch.session.SessionManager;
import io.torch.util.ChannelVariable;
import io.torch.util.Configuration;

/**
 * The server's entry class.
 */
public class Server {

    private final Configuration config = new Configuration();
    private final RouteManager routeManager;
    private final SessionManager sessionManager;

    /**
     * Initialize a new TorchWS server.
     */
    public Server() {
        this(new Builder());
    }

    /**
     * Initialize a new TorchWS server on a given port.
     *
     * @param port the target port
     */
    public Server(int port) {
        this(new Builder());
        
        config.setProperty("listener.port", port);
    }
    
    private Server(Builder builder) {
        this.sessionManager = builder.sessionManager;
        this.routeManager = builder.routeManager;
    }

    /**
     * Start the server. Call this method after setting up every configuration/routes correctly.
     *
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ServerInitializer());

            serverBootstrap.childAttr(ChannelVariable.ROUTE_MANAGER.getVariableKey(), routeManager);
            serverBootstrap.childAttr(ChannelVariable.SESSION_MANAGER.getVariableKey(), sessionManager);

            serverBootstrap.bind(config.getInt("listener.port")).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Return the RouteManager. You can define routes here.
     *
     * @return the route manage
     */
    public RouteManager getRouteManager() {
        return routeManager;
    }
    
    /**
     * Use this class to build a new server instance with custom session/route etc... managers.
     */
    public static class Builder {
        private SessionManager sessionManager = new DefaultSessionManager();
        private RouteManager routeManager = new RouteManager(); //Opening the RouteManager need some future work
        
        public void setSessionManager(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
        }
        
        public Server build() {
            return new Server(this);
        }
    }
}
