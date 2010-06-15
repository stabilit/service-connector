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
package com.stabilit.scm.common.cmd;

import com.stabilit.scm.common.scmp.IHasFaultResponse;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.util.MapBean;
import com.stabilit.scm.common.util.ValidatorException;

/**
 * The Class SCMPValidatorException. Occurs when validation of a request fails.
 * 
 * @author JTraber
 */
public class SCMPValidatorException extends ValidatorException implements IHasFaultResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5190062727277529571L;
	/** The attribute bean. */
	private MapBean<String> attrBean;

	/**
	 * Instantiates a new SCMPValidatorException.
	 */
	public SCMPValidatorException() {
		this(SCMPError.VALIDATION_ERROR);
	}

	/**
	 * Instantiates a new SCMPValidatorException.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public SCMPValidatorException(SCMPError errorCode) {
		this.attrBean = new MapBean<String>();
		this.setErrorCode(errorCode);
	}

	/**
	 * Sets the error code.
	 * 
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(SCMPError errorCode) {
		this.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		this.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setAttribute(String name, String value) {
		this.attrBean.setAttribute(name, value);
	}

	/**
	 * Gets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @return the attribute
	 */
	public Object getAttribute(String name) {
		return this.attrBean.getAttribute(name);
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(String messageType) {
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), messageType);
	}

	/** {@inheritDoc} */
	@Override
	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(attrBean.getAttributeMap());
		response.setSCMP(scmpFault);
	}
}
