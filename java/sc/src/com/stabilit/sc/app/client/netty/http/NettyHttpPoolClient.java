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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.message.ISubscribe;
import com.stabilit.sc.pool.Connection;
import com.stabilit.sc.pool.IConnectionPool;
import com.stabilit.sc.pool.IResponseHandler;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpPoolClient implements IClient {

//	private URL url;
	private String sessionId;
//	private ClientBootstrap bootstrap;
//	private Channel channel;
	private IConnectionPool pool;

	public NettyHttpPoolClient(IConnectionPool pool) {
//		this.url = null;
		this.sessionId = null;
//		this.channel = null;
		this.pool = pool;

//		// Configure the client.
//		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
//				Executors.newCachedThreadPool(), Executors
//						.newCachedThreadPool()));
//		// Set up the event pipeline factory.
//		this.bootstrap.setPipelineFactory(new HttpClientPipelineFactory());
	}

	@Override
	public void closeSession() throws IOException {

	}

	@Override
	public void destroy() throws Exception {
//		this.channel.close();
//		this.bootstrap.releaseExternalResources();
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void openSession() throws IOException {

	}

	@Override
	public IMessageResult receive(ISubscribe subscribeJob) throws Exception {
//		IMessage callJob = new AsyncCallMessage(subscribeJob);
//		SCOP scop = new SCOP(callJob);
//		scop.setSessionId(this.sessionId);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectStreamHttpUtil.writeObjectOnly(baos, scop);
//		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
//				HttpMethod.POST, this.url.getPath());
//		byte[] buffer = baos.toByteArray();
//		// ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
//		// request.setContent(channelBuffer);
//		// this.resetResponse();
//		// ChannelFuture future = channel.write(request);
//		// future.awaitUninterruptibly();
//		// waitForResponse();
//		// ChannelBuffer content = this.responseMessage.getContent();
//		// buffer = content.array();
//		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
//		if (obj instanceof SCOP) {
//			SCOP ret = (SCOP) obj;
//			String retSessionID = ret.getSessionId();
//			if (retSessionID != null) {
//				this.sessionId = retSessionID;
//			}
//			return (IMessageResult) ret.getBody();
//		}
//		throw new Exception("not found");
		return null;
	}

	@Override
	public IMessageResult sendAndReceive(IMessage job) throws Exception {
//		SCOP scop = new SCOP(job);
//		scop.setSessionId(this.sessionId);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectStreamHttpUtil.writeObjectOnly(baos, scop);
//		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
//				HttpMethod.POST, this.url.getPath());
//		byte[] buffer = baos.toByteArray();
//		request.addHeader("Content-Length", String.valueOf(buffer.length));
//		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
//		request.setContent(channelBuffer);
//		ChannelFuture future = channel.write(request);
//		future.awaitUninterruptibly();
//		
//		HttpResponseHandler handler = channel.getPipeline().get(
//				HttpResponseHandler.class);		
//		ChannelBuffer content = handler.getMessageSync().getContent();
//		
//		buffer = content.array();
//		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
//		if (obj instanceof SCOP) {
//			SCOP ret = (SCOP) obj;
//			String retSessionID = ret.getSessionId();
//			if (retSessionID != null) {
//				this.sessionId = retSessionID;
//			}
//			return (IMessageResult) ret.getBody();
//		}
//		throw new Exception("not found");
		
		Connection conn = pool.borrowConnection();
		SCOP scop = new SCOP(job);
		scop.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scop);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, "http://localhost");
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);	
		
		conn.write(request);
		
		IResponseHandler responseHandler = conn.getRespHandler(); // getMessage sync
		ChannelBuffer content = responseHandler.getMessageSync().getContent();
		
		buffer = content.array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
		if (obj instanceof SCOP) {
			SCOP ret = (SCOP) obj;
			String retSessionID = ret.getSessionId();
			if (retSessionID != null) {
				this.sessionId = retSessionID;
			}
			return (IMessageResult) ret.getBody();
		}
		throw new Exception("not found");
	}

	@Override
	public void setEndpoint(URL url) {
//		this.url = url;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClient#connect()
	 */
	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.app.client.IClient#disconnect()
	 */
	@Override
	public void disconnect() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
