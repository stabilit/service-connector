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

import com.stabilit.scm.common.util.ReverseEnumMap;
import com.stabilit.scm.common.util.ReversibleEnum;

/**
 * The Enum SCMPMsgType. Defines possible message types in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPMsgType implements ReversibleEnum<String, SCMPMsgType> {

	/** The ATTACH. */
	ATTACH("ATT"),
	/** The DETACH. */
	DETACH("DET"),
	/** The INSPECT. */
	INSPECT("INS"),
	/** The MANAGE. */
	MANAGE("MGT"),
	/** The CLN_CREATE_SESSION. */
	CLN_CREATE_SESSION("CCS"),
	/** The SRV_CREATE_SESSION. */
	SRV_CREATE_SESSION("SCS"),
	/** The CLN_DELETE_SESSION. */
	CLN_DELETE_SESSION("CDS"),
	/** The SRV_DELETE_SESSION. */
	SRV_DELETE_SESSION("SDS"),
	/** The SRV_ABORT_SESSION. */
	SRV_ABORT_SESSION("SAS"),
	/** The REGISTER_SERVICE. */
	REGISTER_SERVICE("REG"),
	/** The DEREGISTER_SERVICE. */
	DEREGISTER_SERVICE("DRG"),
	/** The CLN_DATA. */
	CLN_DATA("CDA"),
	/** The SRV_DATA. */
	SRV_DATA("SDA"),
	/** The CLN_ECHO. */
	CLN_ECHO("CEC"),
	/** The SRV_ECHO. */
	SRV_ECHO("SEC"),

	/** The CLN_SUBSCRIBE. */
	CLN_SUBSCRIBE("CSU"),
	/** The SRV_SUBSCRIBE. */
	SRV_SUBSCRIBE("SSU"),
	/** The CLN_CHANGE_SUBSCRIPTION. */
	CLN_CHANGE_SUBSCRIPTION("CHS"),
	/** The SRV_CHANGE_SUBSCRIPTION. */
	SRV_CHANGE_SUBSCRIPTION("SHS"),
	/** The CLN_UNSUBSCRIBE. */
	CLN_UNSUBSCRIBE("CUN"),
	/** The SRV_UNSUBSCRIBE. */
	SRV_UNSUBSCRIBE("SUN"),
	/** The RECEIVE_PUBLICATION. */
	RECEIVE_PUBLICATION("CRP"),
	/** The PUBLISH. */
	PUBLISH("SPU"),
	/** The FILE_DOWNLOAD. */
	FILE_DOWNLOAD("FDO"),
	/** The FILE_UPLOAD. */
	FILE_UPLOAD("FUP"),
	/** The FILE_LIST. */
	FILE_LIST("FLI"),
	/** The UNDEFINED. */
	UNDEFINED("UND");

	/** The name. */
	private String name;
	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, SCMPMsgType> reverseMap = new ReverseEnumMap<String, SCMPMsgType>(
			SCMPMsgType.class);

	/**
	 * Instantiates a SCMPMsgType.
	 * 
	 * @param name
	 *            the name
	 */
	private SCMPMsgType(String name) {
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

	/**
	 * To string.
	 * 
	 * @return the string {@inheritDoc}
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
		return reverseMap.get(messageId);
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	@Override
	public String getValue() {
		return this.getName();
	}

	/**
	 * Reverse.
	 * 
	 * @param messageId
	 *            the message id
	 * @return the sCMP msg type
	 */
	@Override
	public SCMPMsgType reverse(String messageId) {
		return reverseMap.get(messageId);
	}
}
