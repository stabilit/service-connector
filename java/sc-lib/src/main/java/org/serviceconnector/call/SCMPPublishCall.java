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
import org.serviceconnector.scmp.SCMPVersion;

/**
 * The Class SCMPPublishCall. Call publishes a message to clients.
 * 
 * @author JTraber
 */
public class SCMPPublishCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPPublishCall.class);

	/**
	 * Instantiates a new SCMPPublishCall.
	 * 
	 * @param req
	 *            the requester
	 * @param serviceName
	 *            the service name
	 */
	public SCMPPublishCall(IRequester req, String serviceName) {
		super(req, serviceName);
		// SCMP Version for a publish has to match lowest version of clients in field!
		this.requestMessage = new SCMPMessage(SCMPVersion.LOWEST);
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/** {@inheritDoc} **/
	@Override
	public void setRequestBody(Object obj) {
		this.requestMessage.setBody(obj);
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
	 * Sets the mask.
	 * 
	 * @param mask
	 *            the new mask
	 */
	public void setMask(String mask) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.MASK, mask);
	}

	/**
	 * Sets the part size.
	 * 
	 * @param partSize
	 *            the size of the message parts
	 */
	public void setPartSize(int partSize) {
		this.requestMessage.setPartSize(partSize);
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            the new message info
	 */
	public void setMessageInfo(String messageInfo) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
	}

	/**
	 * Sets the cache method.
	 * 
	 * @param cacheMethod
	 *            the new cache method
	 */
	public void setCacheMethod(String cacheMethod) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.CACHING_METHOD, cacheMethod);
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.CACHE_ID, cacheId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.PUBLISH;
	}
}
