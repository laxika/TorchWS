package io.torch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.torch.route.RouteManager;
import io.torch.util.ChannelVariable;
import io.torch.util.Configuration;

/**
 * The server's entry class.
 */
public class Server {

    private final RouteManager container = new RouteManager();
    private final Configuration config = new Configuration();

    /**
     * Initialize a new TorchWS server.
     */
    public Server() {
    }

    /**
     * Initialize a new TorchWS server on a given port.
     * 
     * @param port the target port
     */
    public Server(int port) {
        config.setProperty("listener.port", port);
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

            serverBootstrap.childAttr(ChannelVariable.ROUTE_MANAGER.getVariableKey(), container);

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
        return container;
    }
}
