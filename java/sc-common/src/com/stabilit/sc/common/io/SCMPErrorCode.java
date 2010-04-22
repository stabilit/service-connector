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
package com.stabilit.sc.common.io;

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
	PROTOCOL_MISMATCH("407", "Service Connector protocol mismatches."),
	ALREADY_REGISTERED("408","Already registered."),
	NOT_REGISTERED("409","Not registered."),
	ALREADY_ALLOCATED("410","Already allocated."),
	NOT_ALLOCATED("411","Not allocated."),
	NO_SESSION("412","No session found."),
	SERVER_ERROR("500", "Server error occured."),
	UNKNOWN_SERVICE("501", "Unknown service requested.");
	
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
