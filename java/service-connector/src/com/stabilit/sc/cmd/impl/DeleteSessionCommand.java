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

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

/**
 * @author JTraber
 * 
 */
public class DeleteSessionCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(DeleteSessionCommand.class);

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
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		SCMP scmp = request.getSCMP();

		String sessionId = scmp.getSessionId();
		MapBean<?> mapBean = sessionRegistry.get(sessionId);

		if (mapBean == null) {
			log.debug("command error: no session found for id :" + sessionId);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.NO_SESSION);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) mapBean
				.getAttribute(ServiceRegistryItem.class.getName());

		try {
			serviceRegistryItem.deallocate(scmp);
		} catch (Exception e) {
			log.debug("command error: deallocating failed for scmp: " + scmp);
		}

		sessionRegistry.remove(sessionId);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp.getHeader(SCMPHeaderType.SERVICE_NAME
				.getName()));
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class DeleteSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
				if (!SessionRegistry.getCurrentInstance().containsKey(sessionId)) {
					throw new ValidationException("sessoion does not exists!");
				}
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
