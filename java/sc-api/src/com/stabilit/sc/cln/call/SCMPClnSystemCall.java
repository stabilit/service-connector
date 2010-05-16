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

import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPClnSystemCall. Call causes ending specific threads. Used for testing connection failures.
 * 
 * @author JTraber
 */
public class SCMPClnSystemCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPClnSystemCall.
	 */
	public SCMPClnSystemCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnSystemCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPClnSystemCall(IClient client, SCMPMessage scmpSession) {
		super(client, scmpSession);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient,
	 * com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public ISCMPCall newInstance(IClient client, SCMPMessage scmpSession) {
		return new SCMPClnSystemCall(client, scmpSession);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_SYSTEM;
	}

	/**
	 * Sets the max nodes. Number defines how many nodes echo call should pass through.
	 * 
	 * @param maxNodes
	 *            the new max nodes
	 */
	public void setMaxNodes(int maxNodes) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MAX_NODES, String.valueOf(maxNodes));
	}
	
	/**
	 * Sets the header.
	 * 
	 * @param header
	 *            the header
	 */
	public void setHeader(Map<String, String> header) {
		this.requestMessage.setHeader(header);
	}
}
