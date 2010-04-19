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
public class CommandRequest {
	private IRequest request;
	private IResponse response;
	private ICommand command;

	public CommandRequest(IRequest request, IResponse response) {
		this.request = request;
		this.response = response;
		this.command = null;
	}

	public ICommand readCommand() throws Exception {
		this.request.read();
		this.command = CommandFactory.getCurrentCommandFactory().newCommand(this.request);
		if (this.command == null) {
			return null;
		}
		SCMP scmp = this.request.getSCMP();
		if (scmp == null) {
			return null;
		}
		if (!(scmp.isPart() && this.command instanceof SCOnly)) {
			return this.command;
		}
		SCMPComposite scmpComposite = null;
		while (scmp.isPart()) {
			if (scmpComposite == null) {
			   scmpComposite = new SCMPComposite(scmp, (SCMPPart)scmp);
			}
			String messageId = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName());
			String sequenceNr = scmp.getHeader(SCMPHeaderAttributeKey.SEQUENCE_NR.getName());
			String offset = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_OFFSET.getName());			
			SCMP scmpReply = new SCMPPartReply();
			scmpReply.setHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID.getName(), messageId);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SEQUENCE_NR.getName(), sequenceNr);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET.getName(), offset);
			scmpReply.setMessageType(scmp.getMessageType());
			response.setSCMP(scmpReply);
			response.write();
			request.read();
			scmp = request.getSCMP();
			if (scmp != null) {
				scmpComposite.add(scmp);
			}
		}
		this.request.setSCMP(scmpComposite);
		return this.command;
	}
}
