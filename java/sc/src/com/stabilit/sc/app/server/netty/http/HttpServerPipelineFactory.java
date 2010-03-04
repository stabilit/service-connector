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
package com.stabilit.sc.app.server.netty.http;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.util.HashedWheelTimer;

import com.stabilit.sc.app.server.IHttpServerConnection;
import com.stabilit.sc.app.server.http.handler.IKeepAliveHandler;
import com.stabilit.sc.app.server.http.handler.NettyServerIdleHandler;
import com.stabilit.sc.app.service.handler.NettyServiceHttpRequestHandler;
import com.stabilit.sc.app.service.handler.NettyServiceWriteTimeoutHandler;
import com.stabilit.sc.msg.ISCServiceListener;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1868 $, $Date: 2009-11-03 07:48:39 +0100 (Di, 03 Nov 2009) $
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory {

	private Class<? extends ISCServiceListener> scListenerClass;
	private Class<? extends IKeepAliveHandler> keepAliveHandlerClass;
	private NettyServiceHttpRequestHandler requestHandler;
	private NettyServiceWriteTimeoutHandler writeTimeoutHandler;
	private NettyServerIdleHandler nettyIdleHandler;
	private int readTimeout;
	private int writeTimeout;
	private int keepAliveTimeout;
	private IHttpServerConnection conn;

	public HttpServerPipelineFactory(Class<? extends ISCServiceListener> scListenerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout, IHttpServerConnection conn) {
		super();
		this.scListenerClass = scListenerClass;
		this.keepAliveHandlerClass = keepAliveHandlerClass;
		this.keepAliveTimeout = keepAliveTimeout;
		this.readTimeout = readTimeout;
		this.writeTimeout = writeTimeout;
		this.conn = conn;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ISCServiceListener scListener = scListenerClass.newInstance();
		scListener.setConnection(conn);

		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new HttpRequestDecoder());

		// TODO readTimeOutHandler muss gleich implementiert werden, timeseconds ??
		writeTimeoutHandler = new NettyServiceWriteTimeoutHandler(new HashedWheelTimer(), writeTimeout,
				scListener);
		pipeline.addLast("timeout", writeTimeoutHandler);

		if (keepAliveHandlerClass != null) {
			nettyIdleHandler = new NettyServerIdleHandler(new HashedWheelTimer(), keepAliveTimeout,
					keepAliveHandlerClass.newInstance());
			pipeline.addLast("keepAlive", nettyIdleHandler);
		}
		pipeline.addLast("encoder", new HttpResponseEncoder());
		//requestHandler = new NettyServiceHttpRequestHandler(scListener, conn);
		pipeline.addLast("handler", requestHandler);
		return pipeline;

		// TODO NettyServiceHttpRequestHandler
	}
}
