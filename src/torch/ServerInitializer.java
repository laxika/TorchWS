package torch;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import torch.route.RouteManager;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    
    private RouteManager container = null;
    
    public ServerInitializer(RouteManager container) {
        this.container = container;
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder()); 
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("handler", new ServerHandler(container));
    }
}
