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
package com.stabilit.sc.msg;

/**
 * @author JTraber
 * 
 */
public enum MsgType {

	CONNECT("CONNECT"),
	DISCONNECT("DISCONNECT"),
	KEEP_ALIVE("KEEP_ALIVE"),
	CREATE_SESSION("CREATE_SESSION"),
	DELETE_SESSION("DELETE_SESSION"),
	ABORT_SESSION("ABORT_SESSION"),
	DATA("DATA"),
	SUBSCRIBE("SUBSCRIBE"), 
	UNSUBSCRIBE("UNSUBSCRIBE"),
	CHANGE_SUB("CHANGE_SUB"),
	RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	PUBLISH("PUBLISH"),
	REGISTER_SERVICE("REGISTER_SERVICE"), 
	UNDEFINED("UNDEFINED"),
	ECHO("ECHO");

	private String name;

	private MsgType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	public static MsgType getMsgType(String messageId) {
		return MsgType.valueOf(messageId);
	}
}
