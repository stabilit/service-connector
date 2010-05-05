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
package com.stabilit.sc.srv.cmd;

import com.stabilit.sc.scmp.IFaultResponse;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorException;

/**
 * @author JTraber
 * 
 */
public class SCMPValidatorException extends ValidatorException implements IFaultResponse {

	private static final long serialVersionUID = -5190062727277529571L;
	private MapBean<String> attrBean;

	public SCMPValidatorException() {
		this(SCMPErrorCode.VALIDATION_ERROR);
	}

	public SCMPValidatorException(SCMPErrorCode errorCode) {
		this.attrBean = new MapBean<String>();
		this.setErrorCode(errorCode);
	}

	public void setErrorCode(SCMPErrorCode errorCode) {
		this.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		this.setAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}

	public void setAttribute(String name, String value) {
		this.attrBean.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return this.attrBean.getAttribute(name);
	}

	public void setMessageType(String messageType) {
		this.setAttribute(SCMPHeaderAttributeKey.MSG_TYPE.getName(), messageType);
	}

	@Override
	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(attrBean.getAttributeMap());
		response.setSCMP(scmpFault);
	}
}
