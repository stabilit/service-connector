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
package com.stabilit.sc.cln.call;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPDisconnectCall. Call disconnects on SCMP level.
 * 
 * @author JTraber
 */
public class SCMPDisconnectCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPDisconnectCall.
	 */
	public SCMPDisconnectCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPDisconnectCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPDisconnectCall(IClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPDisconnectCall(client);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.DISCONNECT;
	}
}
