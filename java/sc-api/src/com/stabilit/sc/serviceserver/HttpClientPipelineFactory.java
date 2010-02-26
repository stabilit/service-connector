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
package com.stabilit.sc.serviceserver;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.util.HashedWheelTimer;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.serviceserver.handler.IKeepAliveHandler;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.ITimeoutHandler;
import com.stabilit.sc.serviceserver.handler.NettyIdleHandler;
import com.stabilit.sc.serviceserver.handler.NettyResponseHandler;
import com.stabilit.sc.serviceserver.handler.NettyWriteTimeoutHandler;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1868 $, $Date: 2009-11-03 07:48:39 +0100 (Di, 03 Nov 2009) $
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	private Class<? extends IResponseHandler<IClientConnection>> responseHandlerClass;
	private Class<? extends ITimeoutHandler> timeoutHandlerClass;
	private Class<? extends IKeepAliveHandler> keepAliveHandlerClass;
	private NettyResponseHandler<IClientConnection> responseHandler;
	private NettyWriteTimeoutHandler writeTimeoutHandler;
	private NettyIdleHandler nettyIdleHandler;
	private int readTimeout;
	private int writeTimeout;
	private int keepAliveTimeout;
	private IClientConnection conn;

	public HttpClientPipelineFactory(Class<? extends IResponseHandler<IClientConnection>> responseHandlerClass,
			Class<? extends ITimeoutHandler> timeoutHandlerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout, IClientConnection conn) throws InstantiationException, IllegalAccessException {
		super();

		this.responseHandlerClass = responseHandlerClass;
		this.timeoutHandlerClass = timeoutHandlerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.readTimeout = readTimeout;
		this.writeTimeout = writeTimeout;
		this.conn = conn;
	}

	public HttpClientPipelineFactory(Class<? extends IResponseHandler<IClientConnection>> responseHandlerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, IClientConnection conn)
			throws InstantiationException, IllegalAccessException {
		this.responseHandlerClass = responseHandlerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.conn = conn;
	}

	public HttpClientPipelineFactory(Class<? extends IResponseHandler<IClientConnection>> responseHandlerClass, IClientConnection conn)
			throws InstantiationException, IllegalAccessException {
		this.responseHandlerClass = responseHandlerClass;
		this.conn = conn;
	}

	public ChannelPipeline getPipeline() throws Exception {

		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpResponseDecoder());

		if (timeoutHandlerClass != null) {
			//TODO readTimeOutHandler muss gleich implementiert werden, timeseconds ??
			writeTimeoutHandler = new NettyWriteTimeoutHandler(new HashedWheelTimer(), writeTimeout,
					timeoutHandlerClass.newInstance());
			pipeline.addLast("timeout", writeTimeoutHandler);
		}

		if (keepAliveHandlerClass != null) {
			nettyIdleHandler = new NettyIdleHandler(new HashedWheelTimer(), keepAliveTimeout,
					keepAliveHandlerClass.newInstance());
			pipeline.addLast("keepAlive", nettyIdleHandler);
		}
		pipeline.addLast("encoder", new HttpRequestEncoder());
		responseHandler = new NettyResponseHandler(responseHandlerClass.newInstance(), conn);
		pipeline.addLast("handler", responseHandler);
		return pipeline;
	}
}
