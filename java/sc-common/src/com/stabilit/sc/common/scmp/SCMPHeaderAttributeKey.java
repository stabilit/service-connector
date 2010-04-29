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
package com.stabilit.sc.common.scmp;

/**
 * @author JTraber
 * 
 */
public enum SCMPHeaderAttributeKey {

	MSG_TYPE("msgType"),
	SC_VERSION("scVersion"),
	COMPRESSION("compression"),
	LOCAL_DATE_TIME("localDateTime"),
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	KEEP_ALIVE_INTERVAL("keepAliveInterval"),
	SC_ERROR_CODE("scErrorCode"),
	SC_ERROR_TEXT("scErrorText"),
	APP_ERROR_CODE("appErrorCode"),
	APP_ERROR_TEXT("appErrorText"),
	BODY_LENGTH("bodyLength"),
	CACHE_SIGNATURE("cacheSignature"),
	CACHE_EXPIRATION_DATETIME("cacheExpirationDateTime"),
	IP_ADDRESS_LIST("ipAddressList"),
	PORT_NR("portNr"),
	MASK("mask"),
	MAX_SESSIONS("maxSessions"),
	MSG_INFO("msgInfo"),
	MULTI_THREADED("multiThreaded"),
	NO_DATA("noData"),
	REJECT_SESSION("rejectSession"),
	SERVICE_NAME("serviceName"),
	SESSION_ID("sessionId"),
	SESSION_INFO("sessionInfo"),
	TRANSITIVE("transitive"),
	MESSAGE_ID("messageID"),
	BODY_TYPE("bodyType"),
	MAX_NODES("maxNodes"); 
	
	private String name;
	
	private SCMPHeaderAttributeKey(String name) {
		this.name = name;	
	}

	public String getName() {
		return name;
	}
}
