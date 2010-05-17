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

	APP_ERROR_CODE("appErrorCode"),
	APP_ERROR_TEXT("appErrorText"),
	BODY_LENGTH("bodyLength"),
	BODY_TYPE("bodyType"),
	CACHE_EXPIRATION_DATETIME("cacheExpirationDateTime"),
	CACHE_ID("cacheId"),
	CLIENT_ID("clientId"),
	COMPRESSION("compression"),
	IP_ADDRESS_LIST("ipAddressList"),
	KEEP_ALIVE_INTERVAL("keepAliveInterval"),
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	LOCAL_DATE_TIME("localDateTime"),
	MASK("mask"),
	MAX_NODES("maxNodes"),
	MAX_SESSIONS("maxSessions"),
	MESSAGE_ID("messageID"),
	MSG_INFO("msgInfo"),
	MSG_TYPE("msgType"),
	MULTI_THREADED("multiThreaded"),
	NO_DATA("noData"),
	PORT_NR("portNr"),
	REJECT_SESSION("rejectSession"),
	SCCLIENT_ID("scclientId"),					//TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	SCSERVER_ID("scserverId"),					//TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	SC_ERROR_CODE("scErrorCode"),
	SC_ERROR_TEXT("scErrorText"),
	SC_VERSION("scVersion"),
	SERVER_ID("serverId"),						//TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	SERVICE_NAME("serviceName"),
	SERVICE_REGISTRY_ID("serviceRegistryId"),	//TODO (TRN) (DONE JOT) unknown to me - debug purposes testing multiple clients
	SESSION_ID("sessionId"),
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
