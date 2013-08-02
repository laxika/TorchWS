package torch;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
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
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //p.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576)); //Aggregating the request chunks to one big request
        pipeline.addLast("encoder", new HttpResponseEncoder()); 
        pipeline.addLast("streamer", new ChunkedWriteHandler()); //Used at file downloads
//        pipeline.addLast("deflater", new HttpContentCompressor()); //Gzip the output
        pipeline.addLast("handler", new ServerHandler(container, true)); //Set 2nd var false if SSL
    }
}
