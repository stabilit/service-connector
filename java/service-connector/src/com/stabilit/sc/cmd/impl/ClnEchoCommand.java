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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.scmp.IRequest;
import com.stabilit.sc.common.scmp.IResponse;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPErrorCode;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.scmp.Session;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class ClnEchoCommand extends CommandAdapter implements IPassThrough {

	private static Logger log = Logger.getLogger(ClnEchoCommand.class);

	public ClnEchoCommand() {
		this.commandValidator = new ClnEchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_ECHO;
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
		int maxNodes = scmp.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);
		log.debug("Run command " + this.getKey() + " on Node: " + maxNodes);

		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			scmp.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		if (scmp.getBodyLength() > 0) {
			if (scmp.getBody().toString().length() > 100) {
				System.out.println("ClnEchoCommand body = " + scmp.getBody().toString().substring(0, 100));
			} else {
				System.out.println("ClnEchoCommand body = " + scmp.getBody().toString());
			}
		} else {
			System.out.println("ClnEchoCommand empty body");
		}

		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		Session session = (Session) sessionRegistry.get(scmp.getSessionId());
		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());

		if (serviceRegistryItem == null) {
			log.debug("command error: serviceRegistryItem not found");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.SERVER_ERROR);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		header.remove(SCMPHeaderAttributeKey.MAX_NODES.getName());

		if (maxNodes == 2) {
			result = serviceRegistryItem.srvEcho(scmp);
		} else {
			--maxNodes;
			header.put(SCMPHeaderAttributeKey.MAX_NODES.getName(), String.valueOf(maxNodes));
			result = serviceRegistryItem.clnEcho(scmp);
		}
		result.setMessageType(getKey().getResponseName());
		result.setHeader(SCMPHeaderAttributeKey.SCSERVER_ID, socketAddress.hashCode());
		response.setSCMP(result);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ClnEchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
