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

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls to a backend server.
 * 
 * @author JTraber
 */
public abstract class SCMPServerCallAdapter extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPServerCallAdapter.
	 */
	public SCMPServerCallAdapter() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPServerCallAdapter.
	 * 
	 * @param client
	 *            the client
	 * @param clientMessage
	 *            the client message
	 */
	public SCMPServerCallAdapter(IRequester client, SCMPMessage clientMessage) {
		this.req = client;
		this.scmpSession = null;
		if (clientMessage != null) {
			if (clientMessage.isPart()) {
				// on SC scmpSession might be a part - call to server must be a part too
				this.requestMessage = new SCMPPart();
				this.requestMessage.setHeader(clientMessage.getHeader());
			} else {
				this.requestMessage = new SCMPMessage();
			}
			this.requestMessage.setSessionId(clientMessage.getSessionId());
			this.requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, clientMessage.getServiceName());
		}

		if (this.requestMessage == null) {
			this.requestMessage = new SCMPMessage();
		}
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISCMPCall newInstance(IRequester client, SCMPMessage clientMessage);

}