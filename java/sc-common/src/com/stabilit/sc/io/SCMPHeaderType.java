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
package com.stabilit.sc.io;

/**
 * @author JTraber
 * 
 */
public enum SCMPHeaderType {

	MSG_TYPE("msgType"),
	SCMP_VERSION("scmpVersion"),
	COMPRESSION("compression"),
	LOCAL_DATE_TIME("localDateTime"),
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	KEEP_ALIVE_INTERVAL("keepAliveInterval"),
	SC_ERROR_CODE("scErrorCode"),
	SC_ERROR_TEXT("scErrorText");
	
	private String name;
	
	private SCMPHeaderType(String name) {
		this.name = name;	
	}

	public String getName() {
		return name;
	}
	
	public static SCMPHeaderType getMsgHeaderType(String messageId) {
		return SCMPHeaderType.valueOf(messageId);
	}
}
