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
package com.stabilit.sc.net.server.netty.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.server.IServerConnection;

/**
 * An HTTP server that sends back the content of the received HTTP request in a pretty plaintext form.
 * 
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1783 $, $Date: 2009-10-14 07:46:40 +0200 (Mi, 14 Okt 2009) $
 */
public class NettyTcpServerConnection implements IServerConnection, Runnable {

	private ServerBootstrap bootstrap;
	private Channel channel;
	private String host;
	private int port;

	public NettyTcpServerConnection() {
		this.bootstrap = null;
		this.channel = null;
	}

	@Override
	public void create() {
		// Configure the server.
		this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpServerPipelineFactory());
	}

	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	@Override
	public void runSync() throws InterruptedException {
		this.channel = this.bootstrap.bind(new InetSocketAddress(this.port));

		synchronized (this) {
			wait();
		}
	}

	@Override
	public void run() {
		try {
			runSync();
		} catch (InterruptedException e) {
			//TODO
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() throws Exception {
		this.channel.close();
	}

	@Override
	public IFactoryable newInstance() {
		return new NettyTcpServerConnection();
	}

	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}
}
