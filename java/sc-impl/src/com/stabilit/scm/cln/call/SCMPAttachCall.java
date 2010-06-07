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
package com.stabilit.scm.cln.call;

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.util.DateTimeUtility;

/**
 * The Class SCMPAttachCall. Call attaches on SCMP level.
 * 
 * @author JTraber
 */
public class SCMPAttachCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPAttachCall.
	 */
	public SCMPAttachCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPAttachCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPAttachCall(IRequester client) {
		this.req = client;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		this.setVersion(SCMPMessage.SC_VERSION.toString());
		this.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		super.invoke();
		return this.responseMessage;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req) {
		return new SCMPAttachCall(req);
	}

	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the new version
	 */
	public void setVersion(String version) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
	}

	/** {@inheritDoc} */
	public void setCompression(boolean compression) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}

	/**
	 * Sets the local date time.
	 * 
	 * @param localDateTime
	 *            the new local date time
	 */
	private void setLocalDateTime(String localDateTime) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, localDateTime);
	}

	/**
	 * Sets the keep alive timeout.
	 * 
	 * @param keepAliveTimeout
	 *            the new keep alive timeout
	 */
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT, keepAliveTimeout);
	}

	/**
	 * Sets the keep alive interval.
	 * 
	 * @param keepAliveInterval
	 *            the new keep alive interval
	 */
	public void setKeepAliveInterval(int keepAliveInterval) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL, keepAliveInterval);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.ATTACH;
	}
}
