package com.stabilit.scm.common.net.res.netty.http;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandCallback;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageID;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

public class NettyCommandCallback implements ICommandCallback, ISCMPCallback {
	private ICommand command;
	private IRequest request;
	private IResponse response;
	/** The msg id. */
	private ChannelHandlerContext ctx;
	private SCMPMessageID msgID;


	public NettyCommandCallback(ChannelHandlerContext ctx, ICommand command, IRequest request, IResponse response) {
		this.command = command;
		this.request = request;
		this.response = response;
		this.ctx = ctx;
		this.msgID = new SCMPMessageID();
	}

	@Override
	public void callback(SCMPMessage scmpReply) throws Exception {
		scmpReply.setMessageType(command.getKey().getName());
		SCMPMessage scmpRequest = this.request.getMessage();
		response.setSCMP(scmpReply);
		if (response.isLarge()) {
			// response is large, create a large response for reply
			SCMPCompositeSender compositeSender = new SCMPCompositeSender(response.getSCMP());
			SCMPMessage firstSCMP = compositeSender.getFirst();
			response.setSCMP(firstSCMP);
			msgID.incrementMsgSequenceNr();
			msgID.incrementPartSequenceNr();
			firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		} else {
			SCMPMessage message = response.getSCMP();			
			if (message.isPart() || scmpRequest.isPart()) {
				msgID.incrementPartSequenceNr();
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			} else {
				message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
				msgID.incrementMsgSequenceNr();
			}
		}
		response.write();
		// sets the command request null - request is complete don't need to know about preceding messages any more

		// needed for testing TODO
		if ("true".equals(response.getSCMP().getHeader("kill"))) {
			ctx.getChannel().disconnect();
			return;
		}		
	}
	@Override
	public void callback(Throwable th) {
        // TODO		
	}
}
