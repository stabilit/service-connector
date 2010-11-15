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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;

/**
 * The Enum SCMPError. Defines possible errors in SCM.
 * 
 * @author JTraber
 */
public enum SCMPError {

	/** 4xx errors caused by client */
	/** The bad request. */
	BAD_REQUEST("400", "Bad request. The request could not be understood by the server due to malformed syntax."),
	/** The NOT_FOUND. */
	NOT_FOUND("404", "Not found."),
	/** The REQUEST_TIMEOUT. */
	REQUEST_TIMEOUT("408",
			"Request Timeout. The client did not produce a request within the time that the server was prepared to wait."),

	/** 420 Validation errors */
	/** The header validation error. */
	HV_ERROR("420", "Validation error occured."),
	/** The HV_WRONG_SC_VERSION_FORMAT. */
	HV_WRONG_SC_VERSION_FORMAT("420", "Invalid sc version format."),
	/** The HV_WRONG_SC_RELEASE_NR. */
	HV_WRONG_SC_RELEASE_NR("420", "Invalid sc release nr."),
	/** The HV_WRONG_SC_REVISION_NR. */
	HV_WRONG_SC_REVISION_NR("420", "Invalid sc revision nr."),
	/** The HV_WRONG_SCMP_VERSION_FORMAT. */
	HV_WRONG_SCMP_VERSION_FORMAT("420", "Invalid scmp version format."),
	/** The HV_WRONG_SCMP_VERSION_NR. */
	HV_WRONG_SCMP_VERSION_NR("420", "Invalid scmp version nr."),
	/** The HV_WRONG_SCMP_RELEASE_NR. */
	HV_WRONG_SCMP_RELEASE_NR("420", "Invalid scmp release nr."),
	/** The HV_WRONG_LDT. */
	HV_WRONG_LDT("420", "Parsing localDateTime failed."),
	/** The HV_WRONG_IPLIST_FORMAT. */
	HV_WRONG_IPLIST_FORMAT("420", "Invalid iplist format."),
	/** The HV_WRONG_MAX_SESSIONS. */
	HV_WRONG_MAX_SESSIONS("420", "Invalid maxSessions field."),
	/** The HV_WRONG_MAX_CONNECTIONS. */
	HV_WRONG_MAX_CONNECTIONS("420", "Invalid maxConnections field."),
	/** The HV_WRONG_OPERATION_TIMEOUT. */
	HV_WRONG_OPERATION_TIMEOUT("420", "Invalid operation timeout field."),
	/** The HV_WRONG_ECHO_INTERVAL. */
	HV_WRONG_ECHO_INTERVAL("420", "Invalid echoInterval field."),
	/** The HV_WRONG_PORTNR. */
	HV_WRONG_PORTNR("420", "Invalid portNr field."),
	/** The HV_WRONG_KEEPALIVE_INTERVAL. */
	HV_WRONG_KEEPALIVE_INTERVAL("420", "Invalid keepalive interval field."),
	/** The HV_WRONG_NODATA_INTERVAL. */
	HV_WRONG_NODATA_INTERVAL("420", "Invalid not data interval field."),
	/** The HV_WRONG_MASK. */
	HV_WRONG_MASK("420", "Invalid mask."),
	/** The HV_WRONG_SESSION_INFO. */
	HV_WRONG_SESSION_INFO("420", "Invalid session info field."),
	/** The HV_WRONG_SERVICE_NAME. */
	HV_WRONG_SERVICE_NAME("420", "Invalid service name field."),
	/** The HV_WRONG_MESSAGE_INFO. */
	HV_WRONG_MESSAGE_INFO("420", "Invalid message info field."),
	/** The HV_WRONG_MESSAGE_ID. */
	HV_WRONG_MESSAGE_ID("420", "Invalid message id field."),
	/** The HV_WRONG_REMOTE_FILE_NAME. */
	HV_WRONG_REMOTE_FILE_NAME("420", "Invalid remote file name field."),
	/** The HV_WRONG_MESSAGE_ID. */
	HV_WRONG_SESSION_ID("420", "Invalid session id field."),
	/** The HV_WRONG_SC_ERROR_CODE. */
	HV_WRONG_SC_ERROR_CODE("420", "Invalid sc error code field."),
	/** The HV_WRONG_SC_ERROR_TEXT. */
	HV_WRONG_SC_ERROR_TEXT("420", "Invalid sc error text field."),
	/** The HV_WRONG_APP_ERROR_CODE. */
	HV_WRONG_APP_ERROR_CODE("420", "Invalid application error code field."),
	/** The HV_WRONG_APP_ERROR_TEXT. */
	HV_WRONG_APP_ERROR_TEXT("420", "Invalid application error text field."),
	/** The V_WRONG_CONFIGURATION_FILE_FORMAT. */
	V_WRONG_CONFIGURATION_FILE("420", "Invalid configuration file."),

	/** 5xx errors caused by server */
	/** The SERVER_ERROR. */
	SERVER_ERROR("500", "Server error occured."),
	/** The service is DISABLED. */
	SERVICE_DISABLED("501", "Service is disabled."),
	/** The PROXY_TIMEOUT. */
	PROXY_TIMEOUT(
			"504",
			"Proxy Timeout. The server, while acting as a proxy, did not receive a timely response from the upstream server specified by the URI."),
	/** The UPLOAD_FILE_FAILED. */
	UPLOAD_FILE_FAILED("505", "Uploading file failed."),
	/** 6xx errors caused by service connector */
	SC_ERROR("600", "Service connector error."),
	/** The NO_FREE_SERVER. */
	NO_SERVER("601", "No server."),
	/** The SERVER_ALREADY_REGISTERED for this service. */
	SERVER_ALREADY_REGISTERED("602", "Server already registered for the service."),
	/** The NO_FREE_SESSION. */
	NO_FREE_SESSION("603", "No free session."),
	/** The FRAME_DECODER. */
	FRAME_DECODER("606", "Not possible to decode frame, scmp header line wrong."),
	/** The SESSION_ABORT. */
	SESSION_ABORT("607", "Session aborted."),
	/** The CONNECTION_EXCEPTION. */
	CONNECTION_EXCEPTION("610", "Connection error."),
	/** The CACHE_ERROR. */
	CACHE_ERROR("620", "Cache Error."),
	/** The CACHE_LOADING. */
	CACHE_LOADING("621", "Cache Loading.");

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPError.class);

	/** The error code. Should not be integer because it is transmitted over the line */
	private String errorCode;
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
