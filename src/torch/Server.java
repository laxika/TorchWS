package torch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import torch.route.RouteManager;

/**
 * Initialize the server.
 */
public class Server {
    
    private static final RouteManager container = new RouteManager();

    /**
     * Start the server. Call this method after setting up every configuration/routes correctly.
     *
     * @throws Exception
     */
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ServerInitializer(container));

            serverBootstrap.bind(8080).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public RouteManager getRouteManager() {
        return container;
    }
}
