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
package com.stabilit.sc.app.client.netty.http;

import java.io.ByteArrayInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyHttpClientResponseHandler extends SimpleChannelUpstreamHandler {

	private final BlockingQueue<HttpResponse> answer = new LinkedBlockingQueue<HttpResponse>();
	private IClientListener callback;
	private IPoolConnection conn;
	private boolean sync = false;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();

	public NettyHttpClientResponseHandler(IClientListener callback, IPoolConnection conn) {
		super();
		this.callback = callback;
		this.conn = conn;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
	}

	public HttpResponse getMessageSync() {
		sync = true;
		HttpResponse responseMessage;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() waits until first message gets in queue!
				responseMessage = answer.take();
				sync = false;
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return responseMessage;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		//TODO conn setAvailable oder sowas? weider freigeben! eventuell über HTTPConnectionHandler
		if (sync) {
			answer.offer((HttpResponse) e.getMessage());
		} else {
			HttpResponse httpResponse = (HttpResponse) e.getMessage();
			byte[] buffer = httpResponse.getContent().array();
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			SCMP scmp = new SCMP();
			encoderDecoder.decode(bais, scmp);
			callback.messageReceived(conn, scmp);
		}
		// TODO Keep alives müssen hier ausgesondert werden! bzw. acknowledged! oder eventuell ein handler
		// davor
		// TODO subscribe auch hier handeln ? ? siehe NettyHttpClientResponseHandler_old
	}

	public IClientListener getCallback() {
		return callback;
	}
}
