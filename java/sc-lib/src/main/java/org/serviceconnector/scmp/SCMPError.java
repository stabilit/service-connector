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

	/** The UNDEFINED Error. */
	UNDEFINED("000", "Undefined error."),
	/** 4xx errors caused by incoming request */
	/** The bad request. */
	BAD_REQUEST("400", "Bad request. The incoming request could not be understood due to malformed syntax."),
	/** The SERVICE_NOT_FOUND. */
	SERVICE_NOT_FOUND("401", "Service not found."),
	/** The SESSION_NOT_FOUND. */
	SESSION_NOT_FOUND("402", "Session not found."),
	/** The SESSION_NOT_FOUND. */
	SUBSCRIPTION_NOT_FOUND("403", "Subscription not found."),
	/** The SESSION_NOT_FOUND. */
	SERVER_NOT_FOUND("404", "Server not found."),
	/** The REQUEST_TIMEOUT. */
	REQUEST_TIMEOUT("405", "The SC did not timely respond to the request."),
	/** The REQUEST_ABORT. */
	REQUEST_WAIT_ABORT("406", "Emergency abort of the request"),
	/** The BROKEN_SESSION. */
	BROKEN_SESSION("407", "Session is broken."),
	/** The BROKEN_SUBSCRIPTION. */
	BROKEN_SUBSCRIPTION("408", "Subscription is broken."),

	/** 420-470 Validation errors. (HV = Header Validation) */
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
	HV_WRONG_OPERATION_TIMEOUT("433", "Invalid operationTimeout value."),
	/** The HV_WRONG_ECHO_TIMEOUT. */
	HV_WRONG_ECHO_TIMEOUT("434", "Invalid echoTimeout value."),
	/** The HV_WRONG_ECHO_INTERVAL. */
	HV_WRONG_ECHO_INTERVAL("435", "Invalid echoInterval value."),
	/** The HV_WRONG_PORTNR. */
	HV_WRONG_PORTNR("436", "Invalid portNr."),
	/** The HV_WRONG_KEEPALIVE_INTERVAL. */
	HV_WRONG_KEEPALIVE_INTERVAL("437", "Invalid keepaliveInterval value."),
	/** The HV_WRONG_NODATA_INTERVAL. */
	HV_WRONG_NODATA_INTERVAL("438", "Invalid notDataInterval value."),
	/** The HV_WRONG_MASK. */
	HV_WRONG_MASK("439", "Invalid mask."),
	/** The HV_WRONG_SESSION_INFO. */
	HV_WRONG_SESSION_INFO("440", "Invalid sessionInfo value."),
	/** The HV_WRONG_SERVICE_NAME. */
	HV_WRONG_SERVICE_NAME("441", "Invalid serviceName value."),
	/** The HV_WRONG_MESSAGE_INFO. */
	HV_WRONG_MESSAGE_INFO("442", "Invalid messageInfo value."),
	/** The HV_WRONG_MESSAGE_SEQUENCE_NR. */
	HV_WRONG_MESSAGE_SEQUENCE_NR("443", "Invalid messageSequenceNumber."),
	/** The HV_WRONG_REMOTE_FILE_NAME. */
	HV_WRONG_REMOTE_FILE_NAME("444", "Invalid remoteFileName value."),
	/** The HV_WRONG_MESSAGE_ID. */
	HV_WRONG_SESSION_ID("445", "Invalid sessionId value."),
	/** The HV_WRONG_SC_ERROR_CODE. */
	HV_WRONG_SC_ERROR_CODE("446", "Invalid scErrorCode value."),
	/** The HV_WRONG_SC_ERROR_TEXT. */
	HV_WRONG_SC_ERROR_TEXT("447", "Invalid scErrorText value."),
	/** The HV_WRONG_APP_ERROR_CODE. */
	HV_WRONG_APP_ERROR_CODE("448", "Invalid appErrorCode value."), // not used
	/** The HV_WRONG_APP_ERROR_TEXT. */
	HV_WRONG_APP_ERROR_TEXT("449", "Invalid appErrorText value."), // not used

	/** The V_WRONG_CONFIGURATION_FILE_FORMAT. */
	V_WRONG_CONFIGURATION_FILE("460", "Invalid configuration file."),
	/** The V_WRONG_INSPECT_COMMAND. */
	V_WRONG_INSPECT_COMMAND("461", "Invalid inspect command."),
	/** The V_WRONG_MANAGE_COMMAND. */
	V_WRONG_MANAGE_COMMAND("462", "Invalid manage command."),
	/** The V_SERVICE_TYPE. */
	V_WRONG_SERVICE_TYPE("463", "Invalid service type."),
	/** The V_SERVER_TYPE. */
	V_WRONG_SERVER_TYPE("464", "Invalid server type."),

	/** The SERVER_ERROR. */
	SERVER_ERROR("500", "Server error."),
	/** The service is DISABLED. */
	SERVICE_DISABLED("501", "Service is disabled."),
	/** The OPERATION_TIMEOUT_EXPIRED. */
	OPERATION_TIMEOUT("504", "The server did not timely respond to the request."),
	/** The UPLOAD_FILE_FAILED. */
	UPLOAD_FILE_FAILED("505", "Upload file failed."),
	/** The DOWNLOAD_FILE_FAILED. */
	DOWNLOAD_FILE_FAILED("506", "Download file failed."),
	/** The GET_FILE_LIST. */
	GET_FILE_LIST_FAILED("507", "Get file list failed."),

	/** The SC_ERROR. */
	SC_ERROR("600", "Service Connector error."),
	/** The NO_SERVER. */
	NO_SERVER("601", "No server."),
	/** The NO_FREE_SERVER. */
	NO_FREE_SERVER("602", "No free server available."),
	/** The NO_FREE_CONNECTION. */
	NO_FREE_CONNECTION("603", "No free connection to server available."),
	/** The SERVER_ALREADY_REGISTERED for this service. */
	SERVER_ALREADY_REGISTERED("604", "Server is already registered for the service."),
	/** The FRAME_DECODER. */
	FRAME_DECODER("605", "Unable to decode message, SCMP headline is wrong."),
	/** The SESSION_ABORT. */
	SESSION_ABORT("606", "Session aborted."),
	/** The CONNECTION_EXCEPTION. */
	CONNECTION_EXCEPTION("607", "Connection error."),
	/** The CACHE_ERROR. */
	CACHE_ERROR("608", "Cache error."),
	/** The CACHE_LOADING. */
	CACHE_LOADING("609", "Cache Loading. Retry later"),
	/** The CACHE_MANAGER_ERROR. */
	CACHE_MANAGER_ERROR("610", "Cache Manager error.");

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPError.class);

	/** The error code. Should not be integer because it is transmitted over the line */
	private String errorCode;
	/** The error text. */
	private String errorText;

	/** The REVERSE_MAP, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, SCMPError> REVERSE_MAP = new ReverseEnumMap<String, SCMPError>(SCMPError.class);

	/**
	 * Instantiates a new SCMP error code.
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
	 *            the additional info
	 * @return error text as string
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

	/**
	 * Gets the SCMP error by code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @return the SCMP error by code
	 */
	public static SCMPError getSCMPErrorByCode(String errorCode) {
		SCMPError scmpError = REVERSE_MAP.get(errorCode);
		if (scmpError == null) {
			// errorCode doesn't match to a valid SCMPError
			return SCMPError.UNDEFINED;
		}
		return scmpError;
	}
}
