/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.pool;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;

import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.impl.KeepAliveMessage;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class ClientKeepAliveHandlerWrapper extends IdleStateHandler {

	IKeepAliveHandler handler;
	Connection conn;

	public ClientKeepAliveHandlerWrapper(IKeepAliveHandler handler) {
		super(handler.getTimer(), handler.getReaderIdleTimeSeconds(), handler.getWriterIdleTimeSeconds(),
				handler.getAllIdleTimeSeconds());
		this.handler = handler;
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis)
			throws Exception {
		System.out.println("client: KeepAlive nötig!");
		if(!conn.available()) System.out.println("Server down oder überlastet bzw. Firewall schluckt! -> Conn abbauen!");
		
		IMessage ka = new KeepAliveMessage();
		SCOP scop = new SCOP(ka);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scop);
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.POST, "http://localhost");
		byte[] buffer = baos.toByteArray();
		request.addHeader("Content-Length", String.valueOf(buffer.length));
		ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(buffer);
		request.setContent(channelBuffer);	
		
		conn.write(request);
		//wenn er senden will & connection blockiert -> keine Antwort von KA bekommen -> Server down!
		
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public IKeepAliveHandler getHandler() {
		return handler;
	}	
}
