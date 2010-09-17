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
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.sc.cln.call.ISCMPCall;
import org.serviceconnector.sc.cln.call.SCMPCallAdapter;


/**
 * The Class SCMPPublishCall. Call publishes a message to clients.
 * 
 * @author JTraber
 */
public class SCMPPublishCall extends SCMPCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPPublishCall.class);
	
	/**
	 * Instantiates a new SCMPPublishCall.
	 */
	public SCMPPublishCall() {
		this(null, null);
	}

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
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName) {
		return new SCMPPublishCall(requester, serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.PUBLISH;
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
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MASK, mask);
	}
}
