/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.stabilit.sc.net.client.netty.http;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.util.HashedWheelTimer;

import com.stabilit.sc.client.IConnectionListener;
import com.stabilit.sc.net.handler.IKeepAliveHandler;
import com.stabilit.sc.net.handler.NettyIdleHandler;
import com.stabilit.sc.net.handler.NettyWriteTimeoutHandler;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1868 $, $Date: 2009-11-03 07:48:39 +0100 (Di, 03 Nov 2009) $
 */
public class NettyHttpClientPipelineFactory implements ChannelPipelineFactory {

	private Class<? extends IConnectionListener> scListenerClass;
	private Class<? extends IKeepAliveHandler> keepAliveHandlerClass;
	private NettyHttpClientResponseHandler responseHandler;
	private NettyWriteTimeoutHandler writeTimeoutHandler;
	private NettyIdleHandler nettyIdleHandler;
	private int readTimeout;
	private int writeTimeout;
	private int keepAliveTimeout;

	public NettyHttpClientPipelineFactory() {
		super();

		this.scListenerClass = scListenerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.readTimeout = readTimeout;
		this.writeTimeout = writeTimeout;
	}

	public ChannelPipeline getPipeline() throws Exception {
		IConnectionListener scListener = scListenerClass.newInstance();

		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpResponseDecoder());

		// TODO readTimeOutHandler muss gleich implementiert werden, timeseconds ??
		writeTimeoutHandler = new NettyWriteTimeoutHandler(new HashedWheelTimer(), writeTimeout, scListener);
		pipeline.addLast("timeout", writeTimeoutHandler);

		if (keepAliveHandlerClass != null) {
			nettyIdleHandler = new NettyIdleHandler(new HashedWheelTimer(), keepAliveTimeout,
					keepAliveHandlerClass.newInstance());
			pipeline.addLast("keepAlive", nettyIdleHandler);
		}
		pipeline.addLast("encoder", new HttpRequestEncoder());
		responseHandler = new NettyHttpClientResponseHandler();
		pipeline.addLast("handler", responseHandler);
		return pipeline;
	}
}
