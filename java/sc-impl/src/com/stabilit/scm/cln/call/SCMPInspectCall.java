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
package com.stabilit.scm.cln.call;

import java.net.InetAddress;

import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Class SCMPInspectCall. Call inspects SC. Used for testing to assure operations.
 * 
 * @author JTraber
 */
public class SCMPInspectCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new sCMP inspect call.
	 */
	public SCMPInspectCall() {
		this(null);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		return super.invoke();
	}

	/**
	 * Instantiates a new sCMP inspect call.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPInspectCall(IRequester client) {
		this.req = client;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client) {
		return new SCMPInspectCall(client);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.INSPECT;
	}
}
