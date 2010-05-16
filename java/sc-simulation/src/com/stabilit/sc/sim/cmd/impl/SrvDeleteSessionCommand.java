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
package com.stabilit.sc.sim.cmd.impl;
import javax.xml.bind.ValidationException;

import com.stabilit.sc.cmd.impl.CommandAdapter;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.util.MapBean;

public class SrvDeleteSessionCommand extends CommandAdapter {

	public SrvDeleteSessionCommand() {
		this.commandValidator = new SrvDeleteSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DELETE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry.getCurrentInstance();

		String sessionId = message.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);

		if (mapBean == null) {
			if (LoggerListenerSupport.getInstance().isWarn()) {
				LoggerListenerSupport.getInstance().fireWarn(this, "command error: session is no allocated");  
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.NOT_ALLOCATED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		simSessReg.remove(sessionId);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
				.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvDeleteSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// serviceName
				String serviceName = (String) message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}