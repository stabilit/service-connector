/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln.call;

import java.net.InetAddress;

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * @author JTraber
 */
public class SCMPSrvSubscribeCall extends SCMPServerCallAdapter {

	public SCMPSrvSubscribeCall() {
		this(null, null);
	}

	public SCMPSrvSubscribeCall(IRequester requester, SCMPMessage receivedMessage) {
		super(requester, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke();
		return this.responseMessage;
	}

	@Override
	public ISCMPCall newInstance(IRequester requester, SCMPMessage receivedMessage) {
		return new SCMPSrvSubscribeCall(requester, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_SUBSCRIBE;
	}
}