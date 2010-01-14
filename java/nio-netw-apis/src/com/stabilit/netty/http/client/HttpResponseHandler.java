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
package com.stabilit.netty.http.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	
	private int count;
	private long startTime = System.currentTimeMillis();
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (count < NettyHttpClient.numberOfMsg) {
			sendMessage(ctx.getChannel());
			count++;
			// System.out.println(count);
		} else {
			ctx.getChannel().close();
//			long neededTime = System.currentTimeMillis() - startTime;
//			System.out.println("Job Done in: " + neededTime + " Ms");
//			double neededSeconds = neededTime / 1000D;
//			System.out.println((NettyHttpClient.numberOfMsg / neededSeconds)
//					+ " Messages in 1 second!");
			MoreClientsTest.finishThread();
		}
	}
	
	private void sendMessage(Channel channel) {
		String path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/ThePro.html";
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, path);

		channel.write(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		super.exceptionCaught(ctx, e);
		e.getCause().printStackTrace();
	}
}
