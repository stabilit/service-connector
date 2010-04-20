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
package com.stabilit.sc.srv.cmd;

import javax.script.ScriptContext;

import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPComposite;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPPartReply;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

/**
 * @author JTraber
 * 
 */
public class NettyCommandRequest extends CommandRequest {

	private boolean complete;
	private SCMPComposite scmpComposite;

	/**
	 * @param request
	 * @param response
	 */
	public NettyCommandRequest(IRequest request, IResponse response) {
		super(request, response);
		complete = true;
	}

	public ICommand readCommand(IRequest request, IResponse response) throws Exception {
		this.request = request;
		this.response = response;

		this.request.read();
		this.command = CommandFactory.getCurrentCommandFactory().newCommand(this.request);
		if (this.command == null) {
			return null;
		}

		SCMP scmp = this.request.getSCMP();
		if (scmp == null) {
			return null;
		}

		// request not for SC, forward to server
		if (this.command instanceof SCOnly == false) {
			complete = true;
			return this.command;
		}
		
		if (scmpComposite == null) {
			scmpComposite = new SCMPComposite(scmp, scmp);
		} else {
			scmpComposite.add(scmp);
		}

		// request is part of a chunked message
		if (scmp.isPart()) {
			complete = false;
			String messageId = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID);
			String sequenceNr = scmp.getHeader(SCMPHeaderAttributeKey.SEQUENCE_NR);
			String offset = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_OFFSET);
			SCMP scmpReply = new SCMPPartReply();
			scmpReply.setHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID, messageId);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SEQUENCE_NR, sequenceNr);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, offset);
			scmpReply.setMessageType(scmp.getMessageType());
			response.setSCMP(scmpReply);
		} else { // last request of a chunked message or request not chunked
			complete = true;
			this.request.setSCMP(scmpComposite);
		}		
		return this.command;
	}

	public boolean isComplete() {
		return complete;
	}
}
