package torch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import torch.route.RouteManager;
import torch.util.ChannelVariable;
import torch.util.Configuration;

/**
 * Initialize the server.
 */
public class Server {

    private final RouteManager container = new RouteManager();
    private final Configuration config = new Configuration();
    
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
            serverBootstrap.childHandler(new ServerInitializer());

            serverBootstrap.childAttr(ChannelVariable.ROUTE_MANAGER.getVariableKey(), container);

            serverBootstrap.bind(config.getInt("listener.port")).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public RouteManager getRouteManager() {
        return container;
    }
}
