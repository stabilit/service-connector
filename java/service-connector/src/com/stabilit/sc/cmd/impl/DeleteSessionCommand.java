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
package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.IpAddressList;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.msg.impl.CreateSessionMessage;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.util.ValidatorUtility;

/**
 * @author JTraber
 *
 */
public class DeleteSessionCommand extends CommandAdapter {

	public DeleteSessionCommand() {
		this.commandValidator = new CreateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_CREATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(SCMPMsgType.REQ_CREATE_SESSION.getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class CreateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();

			try {
				// TODO msg in body??
				CreateSessionMessage msg = (CreateSessionMessage) scmp.getBody();

				// ipAddressList
				String ipAddressListString = (String) msg.getAttribute(SCMPHeaderType.IP_ADDRESS_LIST.getName());
				IpAddressList ipAddressList = ValidatorUtility.validateIpAddressList(ipAddressListString);
				request.setAttribute(SCMPHeaderType.IP_ADDRESS_LIST.getName(), ipAddressList);
			
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(SCMPMsgType.REQ_CREATE_SESSION.getResponseName());
				throw validatorException;
			}
		}
	}
}
