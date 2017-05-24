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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPFileDownloadCall.
 */
public class SCMPFileDownloadCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPFileDownloadCall.class);

	/**
	 * Instantiates a new sCMP file download call.
	 *
	 * @param requester the requester
	 * @param serviceName the service name
	 * @param sessionId the session id
	 */
	public SCMPFileDownloadCall(IRequester requester, String serviceName, String sessionId) {
		super(requester, serviceName, sessionId);
	}

	/**
	 * Instantiates a new sCMP file download call.
	 *
	 * @param requester the requester
	 * @param msgToForward the msg to forward
	 */
	public SCMPFileDownloadCall(Requester requester, SCMPMessage msgToForward) {
		super(requester, msgToForward);
	}

	/**
	 * Sets the remote file name.
	 *
	 * @param remoteFileName the new remote file name
	 */
	public void setRemoteFileName(String remoteFileName) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.REMOTE_FILE_NAME, remoteFileName);
	}

	/**
	 * Gets the message type.
	 *
	 * @return the message type {@inheritDoc}
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.FILE_DOWNLOAD;
	}
}
