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
package com.stabilit.scm.common.net.res.netty.tcp;

import com.stabilit.scm.common.scmp.IHasFaultResponse;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class SCMPDecoderException. Occurs when decoding SCMP frame fails.
 * 
 * @author JTraber
 */
public class SCMPDecoderException extends Exception implements IHasFaultResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6537338790870840933L;
	/** The attribute bean. */
	private MapBean<String> faultAttr;

	/**
	 * Instantiates a new SCMPValidatorException.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public SCMPDecoderException(SCMPError errorCode, Throwable cause) {
		this(cause);
		this.setErrorCode(errorCode);
	}

	/**
	 * Instantiates a new sCMP decoder exception.
	 * 
	 * @param msg
	 *            the message
	 */
	public SCMPDecoderException(SCMPError errorCode) {
		this.faultAttr = new MapBean<String>();
		this.faultAttr.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
		this.setErrorCode(errorCode);
	}

	/**
	 * Instantiates a new sCMP decoder exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public SCMPDecoderException(Throwable cause) {
		super(cause);
		this.faultAttr = new MapBean<String>();
		this.faultAttr.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
	}

	/**
	 * Sets the error code.
	 * 
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(SCMPError errorCode) {
		this.faultAttr.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		this.faultAttr.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}

	@Override
	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(faultAttr.getAttributeMap());
		response.setSCMP(scmpFault);
	}
}
