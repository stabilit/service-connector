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
public enum SCMPMsgType {

	CONNECT("CONNECT"),
	DISCONNECT("DISCONNECT"),
	KEEP_ALIVE("KEEP_ALIVE"),
	CREATE_SESSION("CREATE_SESSION"),
	DELETE_SESSION("DELETE_SESSION"),
	ABORT_SESSION("ABORT_SESSION"),
	CLN_DATA("CLN_DATA"),
	SRV_DATA("SRV_DATA"),
	SUBSCRIBE("SUBSCRIBE"), 
	UNSUBSCRIBE("UNSUBSCRIBE"),
	CHANGE_SUBSCRIPTION("CHANGE_SUBSCRIPTION"),
	RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	PUBLISH("PUBLISH"),
	REGISTER_SERVICE("REGISTER_SERVICE"),  
	DEREGISTER_SERVICE("DEREGISTER_SERVICE"),
	ALLOCATE_SESSION("ALLOCATE_SESSION"),
	DEALLOCATE_SESSION("DEALLOCATE_SESSION"),
	UNDEFINED("UNDEFINED"),
	ECHO_SRV("ECHO_SRV"),
	ECHO_SC("ECHO_SC"),
	INSPECT("INSPECT");

	private String name;
	private String requestName;
	private String responseName;

	private SCMPMsgType(String name) {
		this.name = name;
		this.requestName = name;
		this.responseName = name;		
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
