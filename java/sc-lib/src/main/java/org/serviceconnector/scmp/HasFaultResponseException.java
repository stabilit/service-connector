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
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponse;

/**
 * The Class HasFaultResponseException. To inherit for exception classes which save specific information for the response. Used to
 * save data about occurred errors and writing the response on a different level of software architecture.
 * 
 * @author JTraber
 */
public abstract class HasFaultResponseException extends Exception {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(HasFaultResponseException.class);
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3781800906847958120L;
	/** The fault message. */
	protected SCMPMessageFault fault = new SCMPMessageFault(SCMPVersion.LOWEST);

	/**
	 * Instantiates a new checks for fault response exception.
	 * 
	 * @param error
	 *            the error
	 */
	public HasFaultResponseException(SCMPError error) {
		super(error.getErrorText());
		this.fault.setError(error);
	}

	/**
	 * Instantiates a new checks for fault response exception.
	 * 
	 * @param error
	 *            the error
	 * @param additionalInfo
	 *            the additional info
	 */
	public HasFaultResponseException(SCMPError error, String additionalInfo) {
		super(error.getErrorText(additionalInfo));
		this.fault.setError(error, additionalInfo);
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setAttribute(SCMPHeaderAttributeKey key, String value) {
		this.fault.setHeader(key, value);
	}

	/**
	 * Sets the fault response.
	 * 
	 * @param response
	 *            the new fault response
	 */
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

	/**
	 * Tries setting the session id and service name in fault message.
	 * 
	 * @param request
	 *            the new session id and service name
	 */
	public void setSessionIdAndServiceName(IRequest request) {
		// set sid & serviceName for EXC
		try {
			SCMPMessage message;
			message = request.getMessage();
			fault.setHeader(message, SCMPHeaderAttributeKey.SERVICE_NAME);
			fault.setHeader(message, SCMPHeaderAttributeKey.SESSION_ID);
		} catch (Exception e) {
			LOGGER.warn("not possible to set service name in EXC of execute command. " + e.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return super.toString() + " HasFaultResponseException [fault=" + fault + "]";
	}
}
