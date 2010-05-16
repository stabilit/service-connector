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
package com.stabilit.sc.cln.call;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.util.DateTimeUtility;

/**
 * The Class SCMPConnectCall. Call connects on SCMP level.
 * 
 * @author JTraber
 */
public class SCMPConnectCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPConnectCall.
	 */
	public SCMPConnectCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPConnectCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPConnectCall(IClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#invoke()
	 */
	@Override
	public SCMPMessage invoke() throws Exception {
		this.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		super.invoke();
		return this.responseMessage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPConnectCall(client);
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#setCompression(boolean)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CONNECT;
	}
}
