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
package com.stabilit.sc.common.io;

import java.util.Map;

import com.stabilit.sc.common.util.DateTimeUtility;

/**
 * @author JTraber
 * 
 */
public class SCMPFault extends SCMP {

	private static final long serialVersionUID = -4041668035605907106L;

	public SCMPFault() {
		super();
	}
	
	public SCMPFault(Map<String, String> map) {
		this.header = map;
	}

	public SCMPFault(SCMPErrorCode errorCode) {
		setError(errorCode);
	}
	
	public void setLocalDateTime() {
		header.put(SCMPHeaderType.LOCAL_DATE_TIME.getName(), DateTimeUtility.getCurrentTimeZoneMillis());
	}
	
	@Override
	public boolean isFault() {
		return true;
	}

	@Override
	public boolean isReply() {
		return true;
	}

	public void setError(String errorCode, String errorText) {
		header.put(SCMPHeaderType.SC_ERROR_CODE.getName(), errorCode);
		header.put(SCMPHeaderType.SC_ERROR_TEXT.getName(), errorText);
	}

	public void setError(SCMPErrorCode errorCode) {
		header.put(SCMPHeaderType.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		header.put(SCMPHeaderType.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}
}