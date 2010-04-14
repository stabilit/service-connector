/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.srv.cmd;

import com.stabilit.sc.common.io.IFaultResponse;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.util.MapBean;

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
		this.setAttribute(SCMPHeaderAttributeType.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		this.setAttribute(SCMPHeaderAttributeType.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}

	public void setAttribute(String name, String value) {
		this.attrBean.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return this.attrBean.getAttribute(name);
	}

	public void setMessageType(String messageType) {
		this.setAttribute(SCMPHeaderAttributeType.MSG_TYPE.getName(), messageType);
	}

	@Override
	public void setFaultResponse(IResponse response) {
		SCMPFault scmpFault = new SCMPFault(attrBean.getAttributeMap());
		response.setSCMP(scmpFault);
	}
}
