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
package com.stabilit.scm.common.scmp;

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
	/** The AUTH_SESSION_ID. */
	AUTH_SESSION_ID("asi"),
	/** The BODY_TYPE. */
	BODY_TYPE("bty"),
	/** The CACHE_EXPIRATION_DATETIME. */
	CACHE_EXPIRATION_DATETIME("ced"),
	/** The CACHE_ID. */
	CACHE_ID("cid"),
	/** The REQ_ID. */
	CLN_REQ_ID("clnReqId"), 
	/** The SC_REQ_ID. */
	SC_REQ_ID("scReqId"), 
	/** The SC_RES_ID. */
	SC_RES_ID("scResId"), 
	/** The SRV_REQ_ID. */
	SRV_REQ_ID("srvReqId"), 
	/** The SRV_RES_ID. */
	SRV_RES_ID("srvResId"),
	/** The COMPRESSION. */
	COMPRESSION("cmp"),
	/** The ECHO_INTERVAL. */
	ECHO_INTERVAL("eci"),
	/** The ECHO_TIMEOUT. */
	ECHO_TIMEOUT("ect"), 
	/** The IMMEDIAT_CONNECT. */
	IMMEDIATE_CONNECT("imc"), 
	/** The IP_ADDRESS_LIST. */
	IP_ADDRESS_LIST("ipl"),
	/** The KEEP_ALIVE_INTERVAL. */
	KEEP_ALIVE_INTERVAL("kpi"),
	/** The LOCAL_DATE_TIME. */
	LOCAL_DATE_TIME("ldt"),
	/** The MESSAGE_ID. */
	MESSAGE_ID("mid"),
	/** The MSG_INFO. */
	MSG_INFO("min"),
	/** The MSG_TYPE. */
	MSG_TYPE("mty"),
	/** The MASK. */
	MASK("msk"),
	/** The MAX_SESSIONS. */
	MAX_SESSIONS("mxs"),
	/** The NO_DATA. */
	NO_DATA("nod"),
	/** The NO_DATA_INTERVAL. */
	NO_DATA_INTERVAL("noi"),	
	/** The ORIGINAL_MSG_ID. */
	ORIGINAL_MSG_ID("omi"),
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
	//TODO this header fields need to be removed - first eliminate references
	/** The BODY_LENGTH. */
	BODY_LENGTH("bodyLength"),
	/** The CLIENT_ID. */
	CLIENT_ID("clientId"),
	/** The KEEP_ALIVE_TIMEOUT. */
	KEEP_ALIVE_TIMEOUT("keepAliveTimeout"),
	/** The MAX_NODES. */
	MAX_NODES("maxNodes"),
	;

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
