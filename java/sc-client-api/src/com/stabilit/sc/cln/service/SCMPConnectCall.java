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
package com.stabilit.sc.cln.service;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.util.DateTimeUtility;

/**
 * @author JTraber
 * 
 */
public class SCMPConnectCall extends SCMPCallAdapter {

	public SCMPConnectCall() {
		this(null);
	}

	public SCMPConnectCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMP invoke() throws Exception {
		this.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		super.invoke();
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPConnectCall(client);
	}

	public void setVersion(String version) {
		call.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
	}

	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}

	private void setLocalDateTime(String localDateTime) {
		call.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, localDateTime);
	}
	
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		call.setHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT, keepAliveTimeout);
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		call.setHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL, keepAliveInterval);
	}
	
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CONNECT;
	}
}
