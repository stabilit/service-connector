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

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPClnCreateSessionCall. Call tries creating a session to a backend server over a SC.
 * 
 * @author JTraber
 */
public class SCMPClnCreateSessionCall extends SCMPCallAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(SCMPClnCreateSessionCall.class);

	/**
	 * Instantiates a new SCMPClnCreateSessionCall.
	 */
	public SCMPClnCreateSessionCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnCreateSessionCall.
	 * 
	 * @param requester
	 *            the requester to use when invoking call
	 * @param serviceName
	 *            the service name
	 */
	public SCMPClnCreateSessionCall(IRequester requester, String serviceName) {
		super(requester, serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPMessageCallback scmpCallback, int timeoutInMillis) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke(scmpCallback, timeoutInMillis);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName) {
		return new SCMPClnCreateSessionCall(requester, serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_CREATE_SESSION;
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
	 * Sets the echo interval seconds.
	 * 
	 * @param echoInterval
	 *            the new echo interval seconds
	 */
	public void setEchoIntervalSeconds(int echoInterval) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL, echoInterval);
	}

	/**
	 * Sets the compression.
	 * 
	 * @param compressed
	 *            the compression
	 */
	public void setCompressed(boolean compressed) {
		if (compressed) {
			this.requestMessage.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
		}
	}

	/** {@inhertiDoc} **/
	@Override
	public void setRequestBody(Object obj) {
		this.requestMessage.setBody(obj);
	}
}
