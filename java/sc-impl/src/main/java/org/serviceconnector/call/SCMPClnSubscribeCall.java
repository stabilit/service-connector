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

import java.net.InetAddress;

import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Class SCMPClnSubscribeCall. Call tries subscribing to a publish service.
 * 
 * @author JTraber
 */
public class SCMPClnSubscribeCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPClnSubscribeCall.
	 */
	public SCMPClnSubscribeCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnSubscribeCall.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 */
	public SCMPClnSubscribeCall(IRequester requester, String serviceName) {
		super(requester, serviceName);
	}

	/**
	 * Invoke.
	 * 
	 * @param scmpCallback
	 *            the scmp callback
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke(scmpCallback, timeoutInMillis);
	}

	/**
	 * New instance.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 * @return the iSCMP call {@inheritDoc}
	 */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName) {
		return new SCMPClnSubscribeCall(requester, serviceName);
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}

	/**
	 * Gets the message type.
	 * 
	 * @return the message type {@inheritDoc}
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_SUBSCRIBE;
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

	/**
	 * Sets the no data interval seconds.
	 * 
	 * @param noDataInterval
	 *            the new no data interval seconds
	 */
	public void setNoDataIntervalSeconds(int noDataInterval) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, noDataInterval);
	}

	/**
	 * Sets the authentication id.
	 * 
	 * @param authId
	 *            the new authentication id
	 */
	public void setAuthenticationId(String authId) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.AUTH_SESSION_ID, authId);
	}
}