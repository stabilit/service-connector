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
import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * The Enum SCMPError. Defines possible errors in SCM.
 * 
 * @author JTraber
 */
public enum SCMPError implements IReversibleEnum<String, SCMPError> {

	UNDEFINED("000", "Undefined error."),
	/** 4xx errors caused by incomming request */
	/** The bad request. */
	BAD_REQUEST("400", "Bad request. The incomming request could not be understood due to malformed syntax."),
	/** The NOT_FOUND. */
	NOT_FOUND("404", "Not found."), 		// TODO TRN splitt
	/** The REQUEST_TIMEOUT. */
	REQUEST_TIMEOUT("408", "The SC did not timely respond to the request."),
	/** The REQUEST_ABORT. */
	REQUEST_ABORT("409", "Operation request did not terminate in time."),
	/** The BROKEN_SESSION. */
	BROKEN_SESSION("410", "Session is broken."),
	/** The BROKEN_SUBSCRIPTION. */
	BROKEN_SUBSCRIPTION("411", "Subscription is broken."),

	/** 420-450 Validation errors. (HV = Header Validation) */
	HV_ERROR("420", "Validation error."),
	/** The HV_WRONG_SC_VERSION_FORMAT. */
	HV_WRONG_SC_VERSION_FORMAT("421", "Invalid SC version format."),
	/** The HV_WRONG_SC_RELEASE_NR. */
	HV_WRONG_SC_RELEASE_NR("422", "Incompatible SC release nr."),
	/** The HV_WRONG_SC_VERSION_NR. */
	HV_WRONG_SC_VERSION_NR("423", "Incompatible SC version nr."),
	/** The HV_WRONG_SC_REVISION_NR. */
	HV_WRONG_SC_REVISION_NR("424", "Incompatible SC revision nr."),
	/** The HV_WRONG_SCMP_VERSION_FORMAT. */
	HV_WRONG_SCMP_VERSION_FORMAT("425", "Invalid SCMP version format."),
	/** The HV_WRONG_SCMP_RELEASE_NR. */
	HV_WRONG_SCMP_RELEASE_NR("426", "Incompatible SCMP release nr."),
	/** The HV_WRONG_SCMP_VERSION_NR. */
	HV_WRONG_SCMP_VERSION_NR("427", "Incompatible SCMP version nr."),	
	/** The HV_WRONG_LDT. */
	HV_WRONG_LDT("428", "Invalid localDateTime format."),
	/** The HV_WRONG_CED. */
	HV_WRONG_CED("429", "Invalid cacheExpirationDateTime format."),
	/** The HV_WRONG_IPLIST_FORMAT. */
	HV_WRONG_IPLIST("430", "Invalid ipList value."),
	/** The HV_WRONG_MAX_SESSIONS. */
	HV_WRONG_MAX_SESSIONS("431", "Invalid maxSessions value."),
	/** The HV_WRONG_MAX_CONNECTIONS. */
	HV_WRONG_MAX_CONNECTIONS("432", "Invalid maxConnections value."),
	/** The HV_WRONG_OPERATION_TIMEOUT. */
	HV_WRONG_OPERATION_TIMEOUT("433", "Invalid operation timeout value."),
	/** The HV_WRONG_ECHO_TIMEOUT. */
	HV_WRONG_ECHO_TIMEOUT("434", "Invalid echoTimeout value."),
	/** The HV_WRONG_ECHO_INTERVAL. */
	HV_WRONG_ECHO_INTERVAL("435", "Invalid echoInterval value."),
	/** The HV_WRONG_PORTNR. */
	HV_WRONG_PORTNR("436", "Invalid portNr field."),
	/** The HV_WRONG_KEEPALIVE_INTERVAL. */
	HV_WRONG_KEEPALIVE_INTERVAL("437", "Invalid keepalive interval value."),
	/** The HV_WRONG_NODATA_INTERVAL. */
	HV_WRONG_NODATA_INTERVAL("438", "Invalid not data interval value."),
	/** The HV_WRONG_MASK. */
	HV_WRONG_MASK("439", "Invalid mask."),
	/** The HV_WRONG_SESSION_INFO. */
	HV_WRONG_SESSION_INFO("440", "Invalid session info value."),
	/** The HV_WRONG_SERVICE_NAME. */
	HV_WRONG_SERVICE_NAME("441", "Invalid service name value."),
	/** The HV_WRONG_MESSAGE_INFO. */
	HV_WRONG_MESSAGE_INFO("442", "Invalid message info value."),
	/** The HV_WRONG_MESSAGE_SEQUENCE_NR. */
	HV_WRONG_MESSAGE_SEQUENCE_NR("443", "Invalid message sequence number value."),
	/** The HV_WRONG_REMOTE_FILE_NAME. */
	HV_WRONG_REMOTE_FILE_NAME("444", "Invalid remote file name value."),
	/** The HV_WRONG_MESSAGE_ID. */
	HV_WRONG_SESSION_ID("445", "Invalid session id value."),
	/** The HV_WRONG_SC_ERROR_CODE. */
	HV_WRONG_SC_ERROR_CODE("446", "Invalid sc error code value."),
	/** The HV_WRONG_SC_ERROR_TEXT. */
	HV_WRONG_SC_ERROR_TEXT("447", "Invalid sc error text value."),
	/** The HV_WRONG_APP_ERROR_CODE. */
	HV_WRONG_APP_ERROR_CODE("448", "Invalid application error code value."),	// not used
	/** The HV_WRONG_APP_ERROR_TEXT. */
	HV_WRONG_APP_ERROR_TEXT("449", "Invalid application error text value."),	// not used
	/** The V_WRONG_CONFIGURATION_FILE_FORMAT. */
	V_WRONG_CONFIGURATION_FILE("450", "Invalid configuration file."),


	/** The SERVER_ERROR. */
	SERVER_ERROR("500", "Server error occured."),
	/** The service is DISABLED. */
	SERVICE_DISABLED("501", "Service is disabled."),
	/** The OPERATION_TIMEOUT_EXPIRED. */
	OPERATION_TIMEOUT_EXPIRED("504", "The server did not timely respond to the request."),
	/** The UPLOAD_FILE_FAILED. */
	UPLOAD_FILE_FAILED("505", "Uploading file failed."),

	
	/** The SC_ERROR. */
	SC_ERROR("600", "Service connector error."),
	/** The NO_SERVER. */
	NO_SERVER("601", "No server."),
	/** The NO_FREE_SERVER. */
	NO_FREE_SERVER("603", "No free server available."),
	/** The NO_FREE_CONNECTION. */
	NO_FREE_CONNECTION("608", "No free connection to server available."),
	/** The SERVER_ALREADY_REGISTERED for this service. */
	SERVER_ALREADY_REGISTERED("602", "Server already registered for the service."),
	/** The FRAME_DECODER. */
	FRAME_DECODER("606", "Unable to decode frame, SCMP headline is wrong."),
	/** The SESSION_ABORT. */
	SESSION_ABORT("607", "Session aborted."),
	/** The CONNECTION_EXCEPTION. */
	CONNECTION_EXCEPTION("610", "Connection error."),
	/** The CACHE_ERROR. */
	CACHE_ERROR("620", "Cache Error."),
	/** The CACHE_LOADING. */
	CACHE_LOADING("621", "Cache Loading. Retry later");

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPError.class);

	/** The error code. Should not be integer because it is transmitted over the line */
	private String errorCode;
	/** The error text. */
	private String errorText;

	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, SCMPError> reverseMap = new ReverseEnumMap<String, SCMPError>(SCMPError.class);

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

	/**
	 * Gets the error text plus additional info.
	 * 
	 * @param additionalInfo
	 * @return
	 */
	public String getErrorText(String additionalInfo) {
		return errorText + " [" + additionalInfo + "]";
	}
	
	/** {@inheritDoc} */
	@Override
	public String getValue() {
		return this.errorCode;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPError reverse(String code) {
		return SCMPError.getSCMPErrorByCode(code);
	}

	public static SCMPError getSCMPErrorByCode(String errorCode) {
		SCMPError scmpError = reverseMap.get(errorCode);
		if (scmpError == null) {
			// errorCode doesn't match to a valid SCMPError
			return SCMPError.UNDEFINED;
		}
		return scmpError;
	}
}
