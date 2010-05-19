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
 * The Enum SCMPHeaderAttributeKey. Defines possible header attributes in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPHeaderAttributeKey {

	/** The APP_ERROR_CODE. */
	APP_ERROR_CODE("appErrorCode"),
	/** The APP_ERROR_TEXT. */
	APP_ERROR_TEXT("appErrorText"),
	/** The BODY_LENGTH. */
	BODY_LENGTH("bodyLength"),
	/** The BODY_TYPE. */
	BODY_TYPE("bodyType"),
	/** The CACHE_EXPIRATION_DATETIME. */
	CACHE_EXPIRATION_DATETIME("cacheExpirationDateTime"),
	/** The CACHE_ID. */
	CACHE_ID("cacheId"),
	/** The CLIENT_ID. */
	CLIENT_ID("clientId"),
	/** The COMPRESSION. */
	COMPRESSION("compression"),
	/** The IP_ADDRESS_LIST. */
	IP_ADDRESS_LIST("ipAddressList"),
	/** The KEEP_ALIVE_INTERVAL. */
	KEEP_ALIVE_INTERVAL("keepAliveInterval"),
	/** The KEEP_ALIVE_TIMEOUT. */
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	/** The LOCAL_DATE_TIME. */
	LOCAL_DATE_TIME("localDateTime"),
	/** The MASK. */
	MASK("mask"),
	/** The MAX_NODES. */
	MAX_NODES("maxNodes"),
	/** The MAX_SESSIONS. */
	MAX_SESSIONS("maxSessions"),
	/** The MESSAGE_ID. */
	MESSAGE_ID("messageID"),
	/** The MSG_INFO. */
	MSG_INFO("msgInfo"),
	/** The MSG_TYPE. */
	MSG_TYPE("msgType"),
	/** The MULTI_THREADED. */
	MULTI_THREADED("multiThreaded"),
	/** The NO_DATA. */
	NO_DATA("noData"),
	/** The PORT_NR. */
	PORT_NR("portNr"),
	/** The REJECT_SESSION. */
	REJECT_SESSION("rejectSession"),
	/** The SCCLIENT_ID. */
	SCCLIENT_ID("scclientId"), // TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	/** The SCSERVER_ID. */
	SCSERVER_ID("scserverId"), // TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	/** The SC_ERROR_CODE. */
	SC_ERROR_CODE("scErrorCode"),
	/** The SC_ERROR_TEXT. */
	SC_ERROR_TEXT("scErrorText"),
	/** The SC_VERSION. */
	SC_VERSION("scVersion"),
	/** The SERVER_ID. */
	SERVER_ID("serverId"), // TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	/** The SERVICE_NAME. */
	SERVICE_NAME("serviceName"),
	/** The SERVICE_REGISTRY_IDd. */
	SERVICE_REGISTRY_ID("serviceRegistryId"), // TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	/** The SESSION_ID. */
	SESSION_ID("sessionId"),
	/** The SESSION_INFO. */
	SESSION_INFO("sessionInfo");

	/** The name. */
	private String name;

	/**
	 * Instantiates a new sCMP header attribute key.
	 * 
	 * @param name
	 *            the name
	 */
	private SCMPHeaderAttributeKey(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
