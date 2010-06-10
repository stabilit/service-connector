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

import java.util.Map;

import com.stabilit.scm.cln.service.ISCSession;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPClnSystemCall. Call causes ending specific threads. Used for testing connection failures.
 * 
 * @author JTraber
 */
public class SCMPClnSystemCall extends SCMPSessionCallAdapter {

	/**
	 * Instantiates a new SCMPClnSystemCall.
	 */
	public SCMPClnSystemCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnSystemCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param scSession
	 *            the scmp session
	 */
	public SCMPClnSystemCall(IRequester req, ISCSession scSession) {
		super(req, scSession);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, ISCSession scSession) {
		return new SCMPClnSystemCall(req, scSession);
	}

	/** {@inheritDoc} */
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
