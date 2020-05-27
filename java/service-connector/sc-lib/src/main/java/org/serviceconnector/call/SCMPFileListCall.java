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

import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SCMPFileListCall.
 */
public class SCMPFileListCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPFileListCall.class);

	/**
	 * Instantiates a new sCMP file list call.
	 *
	 * @param requester the requester
	 * @param serviceName the service name
	 */
	public SCMPFileListCall(IRequester requester, String serviceName) {
		super(requester, serviceName);
	}

	/**
	 * Instantiates a new sCMP file list call.
	 *
	 * @param requester the requester
	 * @param msgToForward the message to forward
	 */
	public SCMPFileListCall(Requester requester, SCMPMessage msgToForward) {
		super(requester, msgToForward);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.FILE_LIST;
	}
}
