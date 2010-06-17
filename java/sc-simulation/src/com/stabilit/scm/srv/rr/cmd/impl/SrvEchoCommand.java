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
package com.stabilit.scm.srv.rr.cmd.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.cmd.impl.CommandAdapter;

public class SrvEchoCommand extends CommandAdapter {

	public SrvEchoCommand() {
		this.commandValidator = new SrvEchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_ECHO;
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		Map<String, String> header = message.getHeader();

		SCMPMessage result = null;

		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getLocalSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		if (message.getBodyLength() > 0) {
			if (message.getBody().toString().length() > 100) {
				System.out.println("SrvEchoCommand body = " + message.getBody().toString().substring(0, 100));
			} else {
				System.out.println("SrvEchoCommand body = " + message.getBody().toString());
			}
		} else {
			System.out.println("SrvEchoCommand empty body");
		}

		result = new SCMPMessage();
		result.setIsReply(true);
		result.setBody(message.getBody());
		result.setSessionId(message.getSessionId());
		result.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		result.setHeader(SCMPHeaderAttributeKey.SRV_RES_ID, request.getRemoteSocketAddress().hashCode());
		result.setMessageType(getKey().getName());
		response.setSCMP(result);
	}

	public class SrvEchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
