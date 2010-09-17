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
package org.serviceconnector.call;

import org.apache.log4j.Logger;
import org.serviceconnector.common.net.req.IRequester;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.sc.cln.call.ISCMPCall;


/**
 * The Class SCMPClnUnsubscribeCall. Call unsubscribes form a publish service.
 * 
 * @author JTraber
 */
public class SCMPClnUnsubscribeCall extends SCMPSessionCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPClnUnsubscribeCall.class);
	
	/**
	 * Instantiates a new SCMPClnUnsubscribeCall.
	 */
	public SCMPClnUnsubscribeCall() {
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPClnUnsubscribeCall.
	 * 
	 * @param req
	 *            the requester
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPClnUnsubscribeCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, String serviceName, String sessionId) {
		return new SCMPClnUnsubscribeCall(req, serviceName, sessionId);
	}
}
