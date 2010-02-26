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
package com.stabilit.sc.serviceserver.handler;

import java.io.ByteArrayInputStream;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyResponseHandler<T extends IConnection> extends SimpleChannelUpstreamHandler {

	private IResponseHandler<T> callback;
	private T conn;

	public NettyResponseHandler(IResponseHandler<T> callback, T conn) {
		super();
		this.callback = callback;
		this.conn = conn;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
		HttpResponse httpResponse = (HttpResponse) e.getMessage();
		byte[] buffer = httpResponse.getContent().array();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
		
		SCMP scmp = (SCMP) obj;
		callback.messageReceived(conn, scmp.getBody());
		
		//TODO Keep alives müssen hier ausgesondert werden! bzw. acknowledged! oder eventuell ein handler davor
		//TODO subscribe auch hier handeln ? ? 
	}

	public IResponseHandler<T> getCallback() {
		return callback;
	}
}
