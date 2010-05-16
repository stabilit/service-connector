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
package com.stabilit.sc.scmp;

import java.util.Map;

import com.stabilit.sc.util.DateTimeUtility;

/**
 * The Class SCMPFault. Indicates an error and causes the <code>SCMPHeadlineKey.EXC</code> on the wire protocol.
 */
public class SCMPFault extends SCMPMessage {

	/**
	 * Instantiates a new SCMP fault.
	 */
	public SCMPFault() {
		super();
	}

	/**
	 * Instantiates a new SCMP fault message.
	 * 
	 * @param map
	 *            the map
	 */
	public SCMPFault(Map<String, String> map) {
		this.header = map;
	}

	/**
	 * Instantiates a new SCMP fault message.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public SCMPFault(SCMPError errorCode) {
		setError(errorCode);
	}

	/**
	 * Sets the local date time.
	 */
	public void setLocalDateTime() {
		header.put(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName(), DateTimeUtility.getCurrentTimeZoneMillis());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isFault()
	 */
	@Override
	public boolean isFault() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isReply()
	 */
	@Override
	public boolean isReply() {
		return true;
	}

	/**
	 * Sets the error code and text.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorText
	 *            the error text
	 */
	public void setError(String errorCode, String errorText) {
		header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode);
		header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorText);
	}

	/**
	 * Sets the error code and text based on scmp error  
	 * 
	 * @param scmpError
	 *            the new error code
	 */
	public void setError(SCMPError scmpError) {
		header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), scmpError.getErrorCode());
		header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), scmpError.getErrorText());
	}
}