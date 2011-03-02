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
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPClnExecuteCall. Call sends data to backend server over SC.
 * 
 * @author JTraber
 */
public class SCMPClnExecuteCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPClnExecuteCall.class);

	/**
	 * Instantiates a new SCMPClnExecuteCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPClnExecuteCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/**
	 * Instantiates a new sCMP cln execute call.
	 * 
	 * @param req
	 *            the req
	 * @param msgToSend
	 *            the message to send
	 */
	public SCMPClnExecuteCall(IRequester req, SCMPMessage msgToSend) {
		super(req, msgToSend);
	}

	/**
	 * Gets the message type.
	 * 
	 * @return the message type {@inheritDoc}
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            the new message info
	 */
	public void setMessageInfo(String messageInfo) {
		if (messageInfo == null) {
			return;
		}
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		if (cacheId == null) {
			return;
		}
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.CACHE_ID, cacheId);
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

	/**
	 * Sets the request body.
	 * 
	 * @param obj
	 *            the new request body {@inheritDoc} *
	 */
	@Override
	public void setRequestBody(Object obj) {
		this.requestMessage.setBody(obj);
	}
}
