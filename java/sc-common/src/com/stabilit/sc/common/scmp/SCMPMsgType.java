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
public enum SCMPMsgType {

	CONNECT("CONNECT"),
	DISCONNECT("DISCONNECT"),
	KEEP_ALIVE("KEEP_ALIVE"),
	CLN_CREATE_SESSION("CLN_CREATE_SESSION"),
	SRV_CREATE_SESSION("SRV_CREATE_SESSION"),
	CLN_DELETE_SESSION("CLN_DELETE_SESSION"),
	SRV_DELETE_SESSION("SRV_DELETE_SESSION"),
	SRV_ABORT_SESSION("SRV_ABORT_SESSION"),
	CLN_DATA("CLN_DATA"),
	SRV_DATA("SRV_DATA"),
	SUBSCRIBE("SUBSCRIBE"), 
	UNSUBSCRIBE("UNSUBSCRIBE"),
	CHANGE_SUBSCRIPTION("CHANGE_SUBSCRIPTION"),
	RECEIVE_PUBLICATION("RECEIVE_PUBLICATION"),
	PUBLISH("PUBLISH"),
	REGISTER_SERVICE("REGISTER_SERVICE"),  
	DEREGISTER_SERVICE("DEREGISTER_SERVICE"),
	UNDEFINED("UNDEFINED"),
	CLN_ECHO("CLN_ECHO"),
	SRV_ECHO("SRV_ECHO"),
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
