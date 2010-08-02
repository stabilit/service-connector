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
package com.stabilit.scm.common.call;

import java.net.InetAddress;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.cln.call.SCMPCallAdapter;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * @author JTraber
 */
public class SCMPClnSubscribeCall extends SCMPCallAdapter {

	public SCMPClnSubscribeCall() {
		this(null, null);
	}

	public SCMPClnSubscribeCall(IRequester requester, String serviceName) {
		super(requester, serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPCallback scmpCallback) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke(scmpCallback);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName) {
		return new SCMPClnSubscribeCall(requester, serviceName);
	}

	public void setSessionInfo(String sessionInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_SUBSCRIBE;
	}

	public void setMask(String mask) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MASK, mask);
	}

	public void setNoDataIntervalSeconds(int noDataInterval) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, noDataInterval);
	}

	public void setAuthenticationId(String authId) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.AUTH_SESSION_ID, authId);
	}
}