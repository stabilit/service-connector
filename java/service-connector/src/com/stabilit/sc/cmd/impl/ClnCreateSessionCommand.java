/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.io.Session;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class ClnCreateSessionCommand extends CommandAdapter implements IPassThrough {

	private static Logger log = Logger.getLogger(ClnCreateSessionCommand.class);

	public ClnCreateSessionCommand() {
		this.commandValidator = new ClnCreateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_CREATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		log.debug("Run command " + this.getKey());
		try {
			// get free service
			SCMP scmp = request.getSCMP();
			String serviceName = scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
			ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

			MapBean<?> mapBean = serviceRegistry.get(serviceName);

			if (mapBean == null) {
				log.debug("command error: unknown service requested");
				SCMPCommandException scmpCommandException = new SCMPCommandException(
						SCMPErrorCode.UNKNOWN_SERVICE);
				scmpCommandException.setMessageType(getKey().getResponseName());
				throw scmpCommandException;
			}
			SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

			// create session
			Session session = new Session();
			scmp.setSessionId(session.getId());
			// try to allocate session
			ServiceRegistryItem serviceRegistryItem = serviceRegistry.allocate(serviceName, scmp);

			// finally save session
			session.setAttribute(ServiceRegistryItem.class.getName(), serviceRegistryItem);
			sessionRegistry.add(session.getId(), session);

			// reply
			SCMPReply scmpReply = new SCMPReply();
			scmpReply.setMessageType(getKey().getResponseName());
			scmpReply.setSessionId(session.getId());
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
			response.setSCMP(scmpReply);
		} catch (Throwable e) {
			log.debug("command error: fatal error when command runs");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.SERVER_ERROR);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ClnCreateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws Exception {
			Map<String, String> scmpHeader = request.getSCMP().getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}

				// ipAddressList
				String ipAddressList = (String) scmpHeader.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST
						.getName());
				ValidatorUtility.validateIpAddressList(ipAddressList);

				// sessionInfo
				String sessionInfo = (String) scmpHeader.get(SCMPHeaderAttributeKey.SESSION_INFO.getName());
				ValidatorUtility.validateString(0, sessionInfo, 256);
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}