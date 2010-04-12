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
package com.stabilit.sc.srv.net.server.netty.tcp;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.common.io.IFaultResponse;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.net.netty.NettyTcpRequest;
import com.stabilit.sc.common.net.netty.NettyTcpResponse;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.registry.ServerRegistry;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyTcpServerRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger log = Logger.getLogger(NettyTcpServerRequestHandler.class);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.error("Exception :" + e.getCause().getMessage());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
		NettyTcpResponse response = new NettyTcpResponse(event);
		IRequest request = new NettyTcpRequest(event);
		try {
			Channel channel = ctx.getChannel();
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			serverRegistry.setThreadLocal(channel.getParent().getId());

			ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(request);
			if (command == null) {
				SCMP scmpReq = request.getSCMP();
				SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
				scmpFault.setMessageType(scmpReq.getMessageType());
				scmpFault.setLocalDateTime();
				response.setSCMP(scmpFault);
				writeResponse(response);
				return;
			}

			ICommandValidator commandValidator = command.getCommandValidator();
			try {
				commandValidator.validate(request, response);
				command.run(request, response);
			} catch (Throwable ex) {
				if (ex instanceof IFaultResponse) {
					((IFaultResponse) ex).setFaultResponse(response);
				}
			}

		//TODO error handling immer antworten?
		} catch (Throwable th) {
			SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		}
		writeResponse(response);
	}

	private void writeResponse(NettyTcpResponse response) throws Exception {
		MessageEvent event = response.getEvent();
		ChannelBuffer buffer = response.getBuffer();

		// Write the response.
		ChannelFuture future = event.getChannel().write(buffer);
	}
}
