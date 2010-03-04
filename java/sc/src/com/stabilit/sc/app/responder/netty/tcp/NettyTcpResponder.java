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
package com.stabilit.sc.app.responder.netty.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.sc.app.server.ITcpServerConnection;
import com.stabilit.sc.app.server.ServerApplication;
import com.stabilit.sc.app.server.http.handler.IKeepAliveHandler;
import com.stabilit.sc.context.ServerApplicationContext;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.msg.ISCServiceListener;

/**
 * An HTTP server that sends back the content of the received HTTP request in a pretty plaintext form.
 * 
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1783 $, $Date: 2009-10-14 07:46:40 +0200 (Mi, 14 Okt 2009) $
 */
public class NettyTcpResponder extends ServerApplication implements ITcpServerConnection {

	private ServerBootstrap bootstrap;
	private Channel channel;

	public NettyTcpResponder() {
		this.bootstrap = null;
		this.channel = null;
	}

	@Override
	public void create() throws Exception {
		// Configure the server.
		this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpResponderPipelineFactory(this));
	}

	public void run() throws Exception {
		ServerApplicationContext appContext = (ServerApplicationContext) this.getContext();
		int port = appContext.getPort();
		this.channel = this.bootstrap.bind(new InetSocketAddress(port));
		synchronized (this) {
			wait();
		}
	}

	@Override
	public void destroy() throws Exception {
		this.channel.close();
	}

	@Override
	public void create(Class<? extends ISCServiceListener> scListenerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void send(SCMP scmp) throws Exception {		
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {	
		return null;
	}

	@Override
	public void connect(Class<? extends IClientListener> scListener) throws ConnectionException {		
	}

	@Override
	public void disconnect() throws Exception {		
	}
}
