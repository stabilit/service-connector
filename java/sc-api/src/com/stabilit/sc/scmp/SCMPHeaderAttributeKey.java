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

	/** The message type. */
	MSG_TYPE("msgType"),
	/** The SC version. */
	SC_VERSION("scVersion"),
	/** The COMPRESSION. */
	COMPRESSION("compression"),
	/** The local date time. */
	LOCAL_DATE_TIME("localDateTime"),
	/** The keep alive timeout. */
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	/** The keep alive interval. */
	KEEP_ALIVE_INTERVAL("keepAliveInterval"),
	/** The SC error code. */
	SC_ERROR_CODE("scErrorCode"),
	/** The SC error text. */
	SC_ERROR_TEXT("scErrorText"),
	/** The application error code. */
	APP_ERROR_CODE("appErrorCode"),
	/** The application error text. */
	APP_ERROR_TEXT("appErrorText"),
	/** The body length. */
	BODY_LENGTH("bodyLength"),
	/** The cache signature. */
	CACHE_SIGNATURE("cacheSignature"),
	/** The cache expiration date time. */
	CACHE_EXPIRATION_DATETIME("cacheExpirationDateTime"),
	/** The IP address list. */
	IP_ADDRESS_LIST("ipAddressList"),
	/** The port number. */
	PORT_NR("portNr"),
	/** The mask. */
	MASK("mask"),
	/** The max sessions. */
	MAX_SESSIONS("maxSessions"),
	/** The message info. */
	MSG_INFO("msgInfo"),
	/** The multiThreaded. */
	MULTI_THREADED("multiThreaded"),
	/** The no data. */
	NO_DATA("noData"),
	/** The reject session. */
	REJECT_SESSION("rejectSession"),
	/** The service name. */
	SERVICE_NAME("serviceName"),
	/** The session id. */
	SESSION_ID("sessionId"),
	/** The session info. */
	SESSION_INFO("sessionInfo"),
	/** The transitive. */
	TRANSITIVE("transitive"),
	/** The message id. */
	MESSAGE_ID("messageID"),
	/** The body type. */
	BODY_TYPE("bodyType"),
	/** The max nodes. */
	MAX_NODES("maxNodes"),
	/** The client id. */
	CLIENT_ID("clientId"),
	/** The SC client id. */
	SCCLIENT_ID("scclientId"),
	/** The SC server id. */
	SCSERVER_ID("scserverId"),
	/** The server id. */
	SERVER_ID("serverId"),
	/** The service registry id. */
	SERVICE_REGISTRY_ID("serviceRegistryId");

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
