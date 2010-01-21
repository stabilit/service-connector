package com.stabilit.netty.timeout.http.client;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 18$, $Date: 2009-11-18:21:+09(Mon, Nov 2009) $
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("decoder", new HttpResponseDecoder());
		// Remove the following line if you don't want automatic content
		// decompression.
		// pipeline.addLast("inflater", new HttpContentDecompressor());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		Timer timer = new HashedWheelTimer();
//		pipeline.addLast("timeout", new WriteTimeoutHandler(timer, 2));
		pipeline.addLast("encoder", new HttpRequestEncoder());
		pipeline.addLast("handler", new HttpResponseHandler());
		return pipeline;
	}
}
