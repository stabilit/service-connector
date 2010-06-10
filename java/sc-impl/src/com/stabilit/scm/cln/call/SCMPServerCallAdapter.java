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
	 * Instantiates a new SCMPServerCallAdapter. Constructor is necessary because in SC you need to hand over received
	 * message because behavior is different if message is of type part.
	 * 
	 * @param req
	 *            the requester
	 * @param message
	 *            the message
	 */
	public SCMPServerCallAdapter(IRequester req, SCMPMessage message) {
		this.requester = req;
		if (message != null) {
			if (message.isPart()) {
				// on SC scmpSession might be a part - call to server must be a part too
				this.requestMessage = new SCMPPart();
				this.requestMessage.setHeader(message.getHeader());
			} else {
				this.requestMessage = new SCMPMessage();
			}
			this.requestMessage.setSessionId(message.getSessionId());
			this.requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message.getServiceName());
		}

		if (this.requestMessage == null) {
			this.requestMessage = new SCMPMessage();
		}
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISCMPCall newInstance(IRequester req, SCMPMessage message);

}