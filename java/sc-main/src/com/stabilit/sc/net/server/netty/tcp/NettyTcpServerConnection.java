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

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.stabilit.sc.app.server.ITcpServerConnection;
import com.stabilit.sc.app.server.NettyTcpResponse;
import com.stabilit.sc.context.ServerConnectionContext;
import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;

/**
 * An HTTP server that sends back the content of the received HTTP request in a pretty plaintext form.
 * 
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * 
 * @version $Rev: 1783 $, $Date: 2009-10-14 07:46:40 +0200 (Mi, 14 Okt 2009) $
 */
public class NettyTcpServerConnection implements ITcpServerConnection {

	private ServerBootstrap bootstrap;
	private Channel channel;
	private ServerConnectionContext serverCtx;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	public NettyTcpServerConnection() {
		this.bootstrap = null;
		this.channel = null;
	}

	@Override
	public void create(ServerConnectionContext serverCtx) {
		this.serverCtx = serverCtx;
		// Configure the server.
		this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyTcpServerPipelineFactory(this));
	}

	public void run() throws Exception {
		
		int port = serverCtx.getServerPort();
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
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		// TODO Achtung Event e nicht vorhanden, übergabe null.... schöner lösen
		NettyTcpResponse response = new NettyTcpResponse(null);
		response.setSCMP(scmp);
		ChannelFuture future = channel.write(response);
		future.awaitUninterruptibly();

		NettyTcpServerRequestHandler handler = channel.getPipeline().get(NettyTcpServerRequestHandler.class);
		ChannelBuffer content = handler.getMessageSync();

		byte[] buffer = content.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		SCMP scmpRe = new SCMP();
		encoderDecoder.decode(bais, scmpRe);

		// String retSessionID = ret.getSessionId();
		// //TODO ??
		// if (retSessionID != null) {
		// this.sessionId = retSessionID;
		// }
		return scmpRe;
	}

	@Override
	public void disconnect() throws Exception {
	}
}
