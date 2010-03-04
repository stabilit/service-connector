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
package com.stabilit.sc.app.client.netty.http;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.util.HashedWheelTimer;

import com.stabilit.sc.app.server.http.handler.IKeepAliveHandler;
import com.stabilit.sc.app.server.http.handler.NettyServerHttpResponseHandler;
import com.stabilit.sc.app.server.http.handler.NettyServerIdleHandler;
import com.stabilit.sc.app.server.http.handler.NettyServerWriteTimeoutHandler;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1868 $, $Date: 2009-11-03 07:48:39 +0100 (Di, 03 Nov 2009) $
 */
public class NettyHttpClientPipelineFactory implements ChannelPipelineFactory {

	private Class<? extends IClientListener> scListenerClass;
	private Class<? extends IKeepAliveHandler> keepAliveHandlerClass;
	private NettyServerHttpResponseHandler responseHandler;
	private NettyServerWriteTimeoutHandler writeTimeoutHandler;
	private NettyServerIdleHandler nettyIdleHandler;
	private int readTimeout;
	private int writeTimeout;
	private int keepAliveTimeout;
	private IPoolConnection conn;

	public NettyHttpClientPipelineFactory(Class<? extends IClientListener> scListenerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout, IPoolConnection conn) {
		super();

		this.scListenerClass = scListenerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.readTimeout = readTimeout;
		this.writeTimeout = writeTimeout;
		this.conn = conn;
	}

	public NettyHttpClientPipelineFactory(Class<? extends IClientListener> scListenerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout,
			IPoolConnection conn) {
		this.scListenerClass = scListenerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.conn = conn;
	}

	public NettyHttpClientPipelineFactory(Class<? extends IClientListener> scListenerClass, IPoolConnection conn) {
		this.scListenerClass = scListenerClass;
		this.conn = conn;
	}

	public ChannelPipeline getPipeline() throws Exception {
		IClientListener scListener = scListenerClass.newInstance();
		scListener.setConnection(conn);

		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpResponseDecoder());

		// TODO readTimeOutHandler muss gleich implementiert werden, timeseconds ??
		writeTimeoutHandler = new NettyServerWriteTimeoutHandler(new HashedWheelTimer(), writeTimeout, scListener);
		pipeline.addLast("timeout", writeTimeoutHandler);

		if (keepAliveHandlerClass != null) {
			nettyIdleHandler = new NettyServerIdleHandler(new HashedWheelTimer(), keepAliveTimeout,
					keepAliveHandlerClass.newInstance());
			pipeline.addLast("keepAlive", nettyIdleHandler);
		}
		pipeline.addLast("encoder", new HttpRequestEncoder());
		responseHandler = new NettyServerHttpResponseHandler(scListener, conn);
		pipeline.addLast("handler", responseHandler);
		return pipeline;
	}
}
