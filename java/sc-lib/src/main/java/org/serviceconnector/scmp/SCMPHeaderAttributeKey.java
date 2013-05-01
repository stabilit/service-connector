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

/**
 * The Enum SCMPHeaderAttributeKey. Defines possible header attributes in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPHeaderAttributeKey {

	/** The APP_ERROR_CODE. */
	APP_ERROR_CODE("aec"),
	/** The APP_ERROR_TEXT. */
	APP_ERROR_TEXT("aet"),
	/** The BODY_TYPE. */
	BODY_TYPE("bty"),
	/** The CACHE_EXPIRATION_DATETIME. */
	CACHE_EXPIRATION_DATETIME("ced"),
	/** The CACHE_ID. */
	CACHE_ID("cid"),
	/** The CACHED. */
	CACHED("cac"),
	/** The CACHE_PARTN_NUMBER. */
	CACHE_PARTN_NUMBER("cpn"),
	/** The CACHING_METHOD. */
	CACHING_METHOD("cmt"),
	/** The NR_OF_APPENDIX. */
	NR_OF_APPENDIX("nra"),
	/** The APPENDIX_NR. */
	APPENDIX_NR("anr"),
	/** The COMPRESSION. */
	COMPRESSION("cmp"),
	/** The ECHO_INTERVAL. */
	ECHO_INTERVAL("eci"),
	/** The OPERATION_TIMEOUT. */
	OPERATION_TIMEOUT("oti"),
	/** The IMMEDIAT_CONNECT. */
	IMMEDIATE_CONNECT("imc"),
	/** The IP_ADDRESS_LIST. */
	IP_ADDRESS_LIST("ipl"),
	/** The KEEP_ALIVE_INTERVAL. */
	KEEP_ALIVE_INTERVAL("kpi"),
	/** The CHECK_REGISTRATION_INTERVAL. */
	CHECK_REGISTRATION_INTERVAL("cri"),
	/** The LOCAL_DATE_TIME. */
	LOCAL_DATE_TIME("ldt"),
	/** The MESSAGE_ID. */
	MESSAGE_SEQUENCE_NR("msn"),
	/** The MSG_INFO. */
	MSG_INFO("min"),
	/** The MSG_TYPE. */
	MSG_TYPE("mty"),
	/** The ACTUAL_MASK. */
	ACTUAL_MASK("ams"),
	/** The MASK. */
	MASK("msk"),
	/** The CASCADED_MASK. */
	CASCADED_MASK("cam"),
	/** The CASCADED_SUBSCRIPTION_ID. */
	CASCADED_SUBSCRIPTION_ID("csi"),
	/** The MAX_SESSIONS. */
	MAX_SESSIONS("mxs"),
	/** The MAX_CONNECTIONS. */
	MAX_CONNECTIONS("mxc"),
	/** The NO_DATA. */
	NO_DATA("nod"),
	/** The NO_DATA_INTERVAL. */
	NO_DATA_INTERVAL("noi"),
	/** The PORT_NR. */
	PORT_NR("pnr"),
	/** The REJECT_SESSION. */
	REJECT_SESSION("rej"),
	/** The REMOTE_FILE_NAME. */
	REMOTE_FILE_NAME("rfn"),
	/** The SC_ERROR_CODE. */
	SC_ERROR_CODE("sec"),
	/** The SC_ERROR_TEXT. */
	SC_ERROR_TEXT("set"),
	/** The SC_VERSION. */
	SC_VERSION("ver"),
	/** The SERVICE_NAME. */
	SERVICE_NAME("nam"),
	/** The SESSION_ID. */
	SESSION_ID("sid"),
	/** The SESSION_INFO. */
	SESSION_INFO("sin"),
	/** The URL_PATH. */
	URL_PATH("urp"),
	/** The UNDEFINED. */
	UNDEFINED("undefined");

	/** The value. */
	private String value;

	/**
	 * Instantiates a SCMPHeaderAttributeKey.
	 * 
	 * @param value
	 *            the value
	 */
	private SCMPHeaderAttributeKey(String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
