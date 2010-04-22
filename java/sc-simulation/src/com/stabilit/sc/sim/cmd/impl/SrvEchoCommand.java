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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class SrvEchoCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(SrvEchoCommand.class);

	public SrvEchoCommand() {
		this.commandValidator = new SrvEchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_ECHO;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMP scmp = request.getSCMP();
		Map<String, String> header = scmp.getHeader();

		SCMP result = null;
		log.debug("Run command " + this.getKey());

		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			scmp.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		if (scmp.getBody().toString().length() > 100) {
			System.out.println("EchoSrvCommand body = " + scmp.getBody().toString().substring(0, 100));
		} else {
			System.out.println("EchoSrvCommand body = " + scmp.getBody().toString());
		}

		if (scmp.isPart()) {
			result = new SCMPPart();
			String messageId = scmp.getHeader(SCMPHeaderAttributeKey.PART_ID);
			result.setHeader(SCMPHeaderAttributeKey.PART_ID, messageId);
			String callLength = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH);
			result.setHeader(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH, callLength);
			String scmpOffset = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_OFFSET);
			result.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, scmpOffset);
		} else {
			result = new SCMPReply();
		}

		result.setBody(scmp.getBody());
		result.setSessionId(scmp.getSessionId());
		result.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);

		result.setMessageType(getKey().getResponseName());
		response.setSCMP(result);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvEchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}
}
