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

import com.stabilit.sc.cmd.impl.CommandAdapter;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class SrvEchoCommand extends CommandAdapter {

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

		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			scmp.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		if (scmp.getBodyLength() > 0) {
			if (scmp.getBody().toString().length() > 100) {
				System.out.println("SrvEchoCommand body = " + scmp.getBody().toString().substring(0, 100));
			} else {
				System.out.println("SrvEchoCommand body = " + scmp.getBody().toString());
			}
		} else {
			System.out.println("SrvEchoCommand empty body");
		}

		result = new SCMPReply();
		result.setBody(scmp.getBody());
		result.setSessionId(scmp.getSessionId());
		result.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		result.setHeader(SCMPHeaderAttributeKey.SERVER_ID, request.getContext().getSocketAddress().hashCode());
		result.setMessageType(getKey().getResponseName());
		response.setSCMP(result);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvEchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
