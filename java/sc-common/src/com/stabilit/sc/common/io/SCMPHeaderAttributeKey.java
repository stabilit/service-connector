/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.common.io;

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
	MESSAGE_INFO("messageInfo"),
	MULTI_THREADED("multiThreaded"),
	NO_DATA("noData"),
	REJECT_SESSION("rejectSession"),
	SEQUENCE_NR("sequenceNr"),
	SERVICE_NAME("serviceName"),
	SESSION_ID("sessionId"),
	SESSION_INFO("sessionInfo"),
	TRANSITIVE("transitive"),
	SCMP_OFFSET("scmpOffset"),
	SCMP_MESSAGE_ID("scmpMessageID"),
	SCMP_CALL_LENGTH("scmpCallLength"),
	SCMP_BODY_TYPE("bodyType"),
	MAX_NODES("maxNodes");
	
	private String name;
	
	private SCMPHeaderAttributeKey(String name) {
		this.name = name;	
	}

	public String getName() {
		return name;
	}
}
