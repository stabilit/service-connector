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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;
import org.serviceconnector.util.DateTimeUtility;

/**
 * The Class SCMPMessageFault. Indicates an error and causes the <code>SCMPHeadlineKey.EXC</code> on the wire protocol.
 */
public class SCMPMessageFault extends SCMPMessage {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPMessageFault.class);

	private Exception exception;

	public SCMPMessageFault(Exception exception) {
		super();
		this.exception = exception;
	}

	/**
	 * Instantiates a new SCMP fault.
	 */
	public SCMPMessageFault() {
		super();
	}

	/**
	 * Instantiates a new SCMP fault message.
	 * 
	 * @param error
	 *            the error code
	 */
	public SCMPMessageFault(SCMPError error) {
		this.setError(error);
	}

	/**
	 * Instantiates a new SCMP fault message.
	 * 
	 * @param error
	 *            the error code
	 * @param additionalInfo
	 *            the additional info
	 */
	public SCMPMessageFault(SCMPError error, String additionalInfo) {
		this.setError(error, additionalInfo);
	}

	/**
	 * Instantiates a new SCMP fault.
	 * 
	 * @param e
	 *            the e
	 * @param error
	 *            the error
	 */
	public SCMPMessageFault(Exception e, SCMPError error) {
		this.setError(error);
		this.exception = e;
	}
	
	/**
	 * Instantiates a new SCMP fault.
	 * 
	 * @param e
	 *            the e
	 * @param error
	 *            the error
	 * @param additionalInfo
	 *            the additional info
	 */
	public SCMPMessageFault(Exception e, SCMPError error, String additionalInfo) {
		this.setError(error, additionalInfo);
		this.exception = e;
	}

	/**
	 * Gets the cause.
	 * 
	 * @return the exception
	 */
	public Exception getCause() {
		return exception;
	}

	/**
	 * Sets the local date time.
	 */
	public void setLocalDateTime() {
		this.header.put(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getValue(), DateTimeUtility.getCurrentTimeZoneMillis());
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFault() {
		return true;
	}

	/** {@inheritDoc} */
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
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getValue(), errorCode);
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getValue(), errorText);
	}

	/**
	 * Sets the error.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorText
	 *            the error text
	 * @param additionalInfo
	 *            the additional info
	 */
	public void setError(SCMPError scmpError, String additionalInfo) {
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getValue(), scmpError.getErrorCode());
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getValue(), scmpError.getErrorText() + " ["
				+ additionalInfo + "]");
	}

	/**
	 * Sets the error code and text based on scmp error.
	 * 
	 * @param scmpError
	 *            the new error code
	 */
	public void setError(SCMPError scmpError) {
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getValue(), scmpError.getErrorCode());
		this.header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getValue(), scmpError.getErrorText());
	}
}