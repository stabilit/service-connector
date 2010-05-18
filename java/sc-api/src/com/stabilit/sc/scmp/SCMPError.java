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
package com.stabilit.sc.scmp;

/**
 * The Enum SCMPError. Defines possible errors in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPError {

	/** The client error. */
	CLIENT_ERROR("400", "Client error occured."),
	/** The request unknown. */
	REQUEST_UNKNOWN("401", "Request unknown"),
	/** The validation error. */
	VALIDATION_ERROR("402", "Validation error occured."),
	/** The not found. */
	NOT_FOUND("404", "Not found error occured."),
	/** The not connected. */
	NOT_CONNECTED("405", "Not connected."),
	/** The already connected. */
	ALREADY_CONNECTED("406", "Already connected."),
	/** The protocol mismatch. */
	PROTOCOL_MISMATCH("407", "Service Connector protocol mismatches."),
	/** The already registered. */
	ALREADY_REGISTERED("408", "Already registered."),
	/** The not registered. */
	NOT_REGISTERED("409", "Not registered."),
	/** The already allocated. */
	ALREADY_ALLOCATED("410", "Already allocated."),
	/** The not allocated. */
	NOT_ALLOCATED("411", "Not allocated."),
	/** The no session found. */
	NO_SESSION("412", "No session found."),
	/** The SERVE r_ error. */
	SERVER_ERROR("500", "Server error occured."),
	/** The unknown service. */
	UNKNOWN_SERVICE("501", "Unknown service requested."),
	/** The connection lost. */
	CONNECTION_LOST("502", "Connection lost.");

	/** The error code. */
	private String errorCode;		//TODO this should be int!
	/** The error text. */
	private String errorText;

	/**
	 * Instantiates a new sCMP error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorText
	 *            the error text
	 */
	private SCMPError(String errorCode, String errorText) {
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	/**
	 * Gets the error code.
	 * 
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the error text.
	 * 
	 * @return the error text
	 */
	public String getErrorText() {
		return errorText;
	}
}
