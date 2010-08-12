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
package com.stabilit.scm.common.scmp;

/**
 * The Class HasFaultResponseException. To inherit for exception classes which save specific information for the
 * response. Used to save data about occurred errors and writing the response on a different level of software
 * architecture.
 * 
 * @author JTraber
 */
public abstract class HasFaultResponseException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3781800906847958120L;
	/** The fault message. */
	protected SCMPFault fault = new SCMPFault();

	public HasFaultResponseException() {
		super();
	}

	public HasFaultResponseException(Throwable cause) {
		super(cause);
	}

	public HasFaultResponseException(SCMPError errorCode) {
		this.fault.setError(errorCode);
	}

	public HasFaultResponseException(SCMPError errorCode, Throwable cause) {
		this(cause);
		this.fault.setError(errorCode);
	}

	public HasFaultResponseException(SCMPError errorCode, String message) {
		super(message);
		this.fault.setError(errorCode);
	}

	public void setAttribute(SCMPHeaderAttributeKey key, String value) {
		this.fault.setHeader(key, value);
	}

	public void setFaultResponse(IResponse response) {
		response.setSCMP(this.fault);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(SCMPMsgType messageType) {
		this.fault.setMessageType(messageType);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageTypeValue
	 *            the new message type
	 */
	public void setMessageType(String messageTypeValue) {
		this.fault.setMessageType(messageTypeValue);
	}
}
