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
package com.stabilit.scm.sc.service;

import java.util.Map;

import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCSessionException.
 * 
 * @author JTraber
 */
public class SCSessionException extends HasFaultResponseException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5911130727633255518L;

	/**
	 * Instantiates a new SCSessionException.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param cause
	 *            the cause
	 */
	public SCSessionException(SCMPError errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * Instantiates a new SCSessionException.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public SCSessionException(SCMPError errorCode) {
		super(errorCode);
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
	}

	/**
	 * Instantiates a new SCSessionException.
	 * 
	 * @param cause
	 *            the cause
	 */
	public SCSessionException(Throwable cause) {
		super(cause);
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
	}

	/**
	 * Instantiates a new sC session exception.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param map
	 *            the map
	 */
	public SCSessionException(SCMPError errorCode, Map<String, String> map) {
		this.faultAttr = map;
		this.setErrorCode(errorCode);
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
	}
}
