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
package com.stabilit.scm.common.call;

import org.apache.log4j.Logger;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPClnExecuteCall. Call sends data to backend server over SC.
 * 
 * @author JTraber
 */
public class SCMPClnExecuteCall extends SCMPSessionCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPClnExecuteCall.class);
	
	/**
	 * Instantiates a new SCMPClnExecuteCall.
	 */
	public SCMPClnExecuteCall() {
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPClnExecuteCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param scSession
	 *            the sc session
	 */
	public SCMPClnExecuteCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client, String serviceName, String sessionId) {
		return new SCMPClnExecuteCall(client, serviceName, sessionId);
	}

	/** {@inheritDoc} */
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
	public void setMessagInfo(String messageInfo) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
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
}
