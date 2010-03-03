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
package com.stabilit.sc.app.server.tcp.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.stabilit.sc.SC;
import com.stabilit.sc.app.server.IHTTPServerConnection;
import com.stabilit.sc.app.server.netty.tcp.NettyTCPRequest;
import com.stabilit.sc.app.server.netty.tcp.NettyTCPResponse;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.RegisterMessage;

/**
 * @author JTraber
 * 
 */
@ChannelPipelineCoverage("one")
public class NettyServerTCPResponseHandler extends SimpleChannelUpstreamHandler {

	private IHTTPServerConnection conn;
	private ICommandFactory commandFactory = CommandFactory.getInstance();
	
	public NettyServerTCPResponseHandler(IHTTPServerConnection conn) {
		this.conn = conn;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		System.out.println("message bekommen im TCP Server on SC!");

		ChannelBuffer chBuffer = (ChannelBuffer) e.getMessage();

		IRequest request = new NettyTCPRequest(chBuffer);
		NettyTCPResponse response = new NettyTCPResponse(e);
		ICommand command = commandFactory.newCommand(request);
		try {
			command.run(request, response);
			//TODO wie lösen??? -----------------
			SCMP scmp = request.getSCMP();
			RegisterMessage registerMessage = (RegisterMessage) scmp.getBody();
			SC.getInstance().quickFix(registerMessage.getServiceName(), conn);
			//-----------------------------
		} catch (CommandException ex) {
			ex.printStackTrace();
		}
		writeResponse(response);
	}
	
    private void writeResponse(NettyTCPResponse response) throws Exception {
    	MessageEvent event = response.getEvent();
        ChannelBuffer buffer = response.getBuffer();
        
        // Write the response.
        ChannelFuture future = event.getChannel().write(buffer);     
    }
}
