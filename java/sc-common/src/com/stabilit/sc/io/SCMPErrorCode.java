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
public enum SCMPErrorCode {

	CLIENT_ERROR("400", "Client error occured."),
	REQUEST_UNKNOWN("401", "Request unknown"),
	VALIDATION_ERROR("402", "Validation error occured."),	
	NOT_FOUND("404", "Not found error occured."),
	NOT_CONNECTED("405", "Not connected."),
	ALREADY_CONNECTED("406","Already connected."),
	PROTOCOL_MISMATCH("405", "Service Connector protocol mismatches."),
	SERVER_ERROR("500", "Server error occured.");
	
	private String errorCode;
	private String errorText;
	
	private SCMPErrorCode(String errorCode, String errorText) {
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorText() {
		return errorText;
	}
}
