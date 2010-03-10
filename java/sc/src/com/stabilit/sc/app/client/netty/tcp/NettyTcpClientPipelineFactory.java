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
package com.stabilit.sc.app.client.netty.tcp;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1868 $, $Date: 2009-11-03 07:48:39 +0100 (Di, 03 Nov 2009) $
 */
public class NettyTcpClientPipelineFactory implements ChannelPipelineFactory {

	private Class<? extends IClientListener> scListenerClass;
	private IPoolConnection conn;

	public NettyTcpClientPipelineFactory(Class<? extends IClientListener> scListenerClass, IPoolConnection conn) {
		super();

		this.scListenerClass = scListenerClass;
		this.conn = conn;
	}

	public ChannelPipeline getPipeline() throws Exception {
		IClientListener scListener = scListenerClass.newInstance();

		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		
		NettyTcpClientResponseHandler responseHandler = new NettyTcpClientResponseHandler(scListener, conn);
		pipeline.addLast("handler", responseHandler);
		return pipeline;
	}
}
