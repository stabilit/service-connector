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
package com.stabilit.sc.api.net.server.netty.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.app.server.ITcpServerConnection;
import com.stabilit.sc.app.server.NettyTcpRequest;
import com.stabilit.sc.app.server.NettyTcpResponse;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.RoundTripMessage;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyAPITcpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private final BlockingQueue<ChannelBuffer> answer = new LinkedBlockingQueue<ChannelBuffer>();
	private ITcpServerConnection conn;
	private ICommandFactory commandFactory = CommandFactory.getInstance();
	private Logger log = Logger.getLogger(NettyAPITcpServerRequestHandler.class);
	private boolean sync = false;

	public NettyAPITcpServerRequestHandler(ITcpServerConnection conn) {
		this.conn = conn;
	}

	ChannelBuffer getMessageSync() {
		sync = true;
		ChannelBuffer responseMessage;
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
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.error("Exception :" + e.getCause().getMessage());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		if (sync) {
			answer.offer((ChannelBuffer) e.getMessage());
		} else {
			ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();

			IRequest request = new NettyTcpRequest(chBuffer);
			NettyTcpResponse response = new NettyTcpResponse(e);
			ICommand command = commandFactory.newCommand(request);

			log.debug("NettyTcpResponderRequestHandler: following command received - " + command.getKey());

			SCMP scmp = request.getSCMP();
			ISession session = request.getSession(true);
			Object obj = scmp.getBody();
			SCMP scmpRes = new SCMP();
			if (obj instanceof IMessage) {
				RoundTripMessage registerMessage = (RoundTripMessage) scmp.getBody();
				scmpRes.setBody(registerMessage);
				registerMessage.setAttribute("msg", registerMessage.getAttribute("msg").toString().toUpperCase());
			} else {
				String registerMessage = (String) scmp.getBody();
				scmpRes.setBody(registerMessage.toUpperCase());
			}
			scmp.setMessageId("roundTrip");
			
			response.setSession(session);
			response.setSCMP(scmpRes);
			writeResponse(response);
		}
	}

	private void writeResponse(NettyTcpResponse response) throws Exception {
		MessageEvent event = response.getEvent();
		ChannelBuffer buffer = response.getBuffer();

		// Write the response.
		ChannelFuture future = event.getChannel().write(buffer);
	}
}
