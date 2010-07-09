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

import java.util.HashMap;
import java.util.Map;

/**
 * The Class HasFaultResponseException. To inherit for exception classes which save specific information for the response. Used
 * to save data about occurred errors and writing the response on a different level of software architecture.
 * 
 * @author JTraber
 */
public abstract class HasFaultResponseException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3781800906847958120L;
	/** The attribute bean. */
	protected Map<String, String> faultAttr = new HashMap<String, String>();	
	
	public HasFaultResponseException() {
		super();
	}

	public HasFaultResponseException(Throwable cause) {
		super(cause);
	}	
	
	public HasFaultResponseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HasFaultResponseException(SCMPError errorCode) {
		this.setErrorCode(errorCode);
	}
	
	public HasFaultResponseException(SCMPError errorCode, Throwable cause) {
		this(cause);
		this.setErrorCode(errorCode);
	}
	
	public HasFaultResponseException(SCMPError errorCode, String message) {
		super(message);
		this.setErrorCode(errorCode);
	}

	public void setErrorCode(SCMPError errorCode) {
		this.faultAttr.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getValue(), errorCode.getErrorCode());
		this.faultAttr.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getValue(), errorCode.getErrorText());
	}
	
	public void setAttribute(String name, String value) {
		this.faultAttr.put(name, value);
	}

	public Object getAttribute(String name) {
		return this.faultAttr.get(name);
	}

	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(faultAttr);
		response.setSCMP(scmpFault);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(SCMPMsgType messageType) {
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getValue(), messageType.getValue());
	}
}
