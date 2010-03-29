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

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPCommandException;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public class DeleteSessionCommand extends CommandAdapter {

	public DeleteSessionCommand() {
		this.commandValidator = new DeleteSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_DELETE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		SCMP scmp = request.getSCMP();

		String sessionId = scmp.getSessionId();
		MapBean<?> mapBean = sessionRegistry.get(sessionId);

		if (mapBean == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.NO_SESSION);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) mapBean
				.getAttribute(ServiceRegistryItem.class.getName());

		try {
			serviceRegistryItem.deallocate(scmp);
		} catch (Exception e) {
			// TODO what to do!
		}

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp
				.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class DeleteSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response)
				throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
				if (SessionRegistry.getCurrentInstance().containsKey(sessionId)) {
					throw new ValidationException("sessoion does not exists!");
				}
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
