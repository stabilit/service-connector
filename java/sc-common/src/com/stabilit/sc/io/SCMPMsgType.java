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
package com.stabilit.sc.io;

/**
 * @author JTraber
 * 
 */
public enum SCMPMsgType {

	REQ_CONNECT("CONNECT"),
	RES_CONNECT("CONNECT"),
	REQ_DISCONNECT("DISCONNECT"),
	RES_DISCONNECT("DISCONNECT"),
	REQ_KEEP_ALIVE("KEEP_ALIVE"),
	RES_KEEP_ALIVE("KEEP_ALIVE"),
	REQ_CREATE_SESSION("CREATE_SESSION"),
	RES_CREATE_SESSION("CREATE_SESSION"),
	REQ_DELETE_SESSION("DELETE_SESSION"),
	RES_DELETE_SESSION("DELETE_SESSION"),
	REQ_ABORT_SESSION("ABORT_SESSION"),
	RES_ABORT_SESSION("ABORT_SESSION"),
	REQ_CLN_DATA("CLN_DATA"),
	RES_CLN_DATA("CLN_DATA"),
	REQ_SRV_DATA("SRV_DATA"),
	RES_SRV_DATA("SRV_DATA"),
	REQ_SUBSCRIBE("SUBSCRIBE"),
	RES_SUBSCRIBE("SUBSCRIBE"), 
	REQ_UNSUBSCRIBE("UNSUBSCRIBE"),
	RES_UNSUBSCRIBE("UNSUBSCRIBE"),
	REQ_CHANGE_SUBSCRIPTION("CHANGE_SUBSCRIPTION"),
	RES_CHANGE_SUBSCRIPTION("CHANGE_SUBSCRIPTION"),
	REQ_RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	RES_RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	REQ_PUBLISH("PUBLISH"),
	RES_PUBLISH("PUBLISH"),
	REQ_REGISTER_SERVICE("REGISTER_SERVICE"), 
	RES_REGISTER_SERVICE("REGISTER_SERVICE"),
	REQ_DEREGISTER_SERVICE("DEREGISTER_SERVICE"), 
	RES_DEREGISTER_SERVICE("DEREGISTER_SERVICE"),
	REQ_ALLOCATE_SESSION("ALLOCATE_SESSION"),
	RES_ALLOCATE_SESSION("ALLOCATE_SESSION"),
	REQ_DEALLOCATE_SESSION("DEALLOCATE_SESSION"),
	RES_DEALLOCATE_SESSION("DEALLOCATE_SESSION"),
	UNDEFINED("UNDEFINED"),
	REQ_ECHO("ECHO"),
	RES_ECHO("ECHO"),
	REQ_MAINTENANCE("MAINTENANCE"),
	RES_MAINTENANCE("MAINTENANCE");

	private static final String RES_PREFIX = "RES_";
	private static final String REQ_PREFIX = "REQ_";
	private String name;
	private String requestName;
	private String responseName;

	private SCMPMsgType(String name) {
		this.name = name;
		this.requestName = REQ_PREFIX + name;
		this.responseName = RES_PREFIX + name;		
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public String getResponseName() {
		return responseName;
	}

	public String toString() {
		return name;
	}

	public static SCMPMsgType getMsgType(String messageId) {
		return SCMPMsgType.valueOf(messageId);
	}
}
