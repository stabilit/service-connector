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
package org.serviceconnector.call;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Class SCMPClnChangeSubscriptionCall. Call changes a subscription for a client.
 * 
 * @author JTraber
 */
public class SCMPClnChangeSubscriptionCall extends SCMPCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPClnChangeSubscriptionCall.class);
	
	/**
	 * Instantiates a new SCMPClnChangeSubscriptionCall.
	 */
	public SCMPClnChangeSubscriptionCall() {
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPClnChangeSubscriptionCall.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 */
	public SCMPClnChangeSubscriptionCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName, String sessionId) {
		return new SCMPClnChangeSubscriptionCall(requester, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_CHANGE_SUBSCRIPTION;
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}

	/**
	 * Sets the mask.
	 * 
	 * @param mask
	 *            the new mask
	 */
	public void setMask(String mask) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MASK, mask);
	}
}