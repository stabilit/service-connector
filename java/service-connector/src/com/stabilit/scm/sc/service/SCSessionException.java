/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.sc.service;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.scm.common.scmp.IHasFaultResponse;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * @author JTraber
 *
 */
public class SCSessionException extends Exception implements IHasFaultResponse {

	private static final long serialVersionUID = -5911130727633255518L;
	/** The attribute bean. */
	private Map<String, String> faultAttr;

	public SCSessionException(SCMPError errorCode, Throwable cause) {
		this(cause);
		this.setErrorCode(errorCode);
	}

	public SCSessionException(SCMPError errorCode) {
		this.faultAttr = new HashMap<String, String>();
		this.faultAttr.put(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
		this.setErrorCode(errorCode);
	}

	public SCSessionException(Throwable cause) {
		super(cause);
		this.faultAttr = new HashMap<String, String>();
		this.faultAttr.put(SCMPHeaderAttributeKey.MSG_TYPE.getName(), SCMPMsgType.UNDEFINED.getName());
	}

	public void setErrorCode(SCMPError errorCode) {
		this.faultAttr.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		this.faultAttr.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}
	
	public void setAttribute(String name, String value) {
		this.faultAttr.put(name, value);
	}

	public Object getAttribute(String name) {
		return this.faultAttr.get(name);
	}

	@Override
	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(faultAttr);
		response.setSCMP(scmpFault);
	}

	public void setAttributeMap(Map<String, String> header) {
		this.faultAttr = header;
	}
}
