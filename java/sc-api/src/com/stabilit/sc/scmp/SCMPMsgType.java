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
 * The Enum SCMPMsgType. Defines possible message types in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPMsgType {

	/** The CONNECT. */
	CONNECT("CONNECT"),
	/** The DISCONNECT. */
	DISCONNECT("DISCONNECT"),
	/** The KEEP ALIVE. */
	KEEP_ALIVE("KEEP_ALIVE"),
	/** The CLN CREATE SESSION. */
	CLN_CREATE_SESSION("CLN_CREATE_SESSION"),
	/** The SRV CREATE SESSION. */
	SRV_CREATE_SESSION("SRV_CREATE_SESSION"),
	/** The CLN DELETE SESSION. */
	CLN_DELETE_SESSION("CLN_DELETE_SESSION"),
	/** The SRV DELETE SESSION. */
	SRV_DELETE_SESSION("SRV_DELETE_SESSION"),
	/** The SRV ABORT SESSION. */
	SRV_ABORT_SESSION("SRV_ABORT_SESSION"),
	/** The CLN DATA. */
	CLN_DATA("CLN_DATA"),
	/** The SRV DATA. */
	SRV_DATA("SRV_DATA"),
	/** The SUBSCRIBE. */
	SUBSCRIBE("SUBSCRIBE"),
	/** The UNSUBSCRIBE. */
	UNSUBSCRIBE("UNSUBSCRIBE"),
	/** The CHANGE SUBSCRIPTION. */
	CHANGE_SUBSCRIPTION("CHANGE_SUBSCRIPTION"),
	/** The RECEIVE PUBLICATION. */
	RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	/** The PUBLISH. */
	PUBLISH("PUBLISH"),
	/** The REGISTER SERVICE. */
	REGISTER_SERVICE("REGISTER_SERVICE"),
	/** The DEREGISTER SERVICE. */
	DEREGISTER_SERVICE("DEREGISTER_SERVICE"),
	/** The UNDEFINED. */
	UNDEFINED("UNDEFINED"),
	/** The CLN ECHO. */
	CLN_ECHO("CLN_ECHO"),
	/** The SRV ECHO. */
	SRV_ECHO("SRV_ECHO"),
	/** The ECHO SC. */
	ECHO_SC("ECHO_SC"),
	/** The INSPECT. */
	INSPECT("INSPECT"),
	/** The CLN SYSTEM. */
	CLN_SYSTEM("CLN_SYSTEM"),
	/** The SRV SYSTEM. */
	SRV_SYSTEM("SRV_SYSTEM");

	/** The name. */
	private String name;
	/** The request name. */
	private String requestName;
	/** The response name. */
	private String responseName;
	
	/**
	 * Instantiates a SCMPMsgType.
	 * 
	 * @param name
	 *            the name
	 */
	private SCMPMsgType(String name) {
		this.name = name;
		this.requestName = name;
		this.responseName = name;
	}

	/**
	 * Gets the request name.
	 * 
	 * @return the request name
	 */
	public String getRequestName() {
		return requestName;
	}

	/**
	 * Gets the response name.
	 * 
	 * @return the response name
	 */
	public String getResponseName() {
		return responseName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * Gets the msg type by string.
	 * 
	 * @param messageId
	 *            the message id
	 * @return the msg type
	 */
	public static SCMPMsgType getMsgType(String messageId) {
		return SCMPMsgType.valueOf(messageId);
	}
}
