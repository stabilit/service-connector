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
	/** The ECHO_SC. */
	ECHO_SC("ECHO_SC"),
	/** The INSPECT. */
	INSPECT("INS"),

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
	/** The CLN_DATA. */
	CLN_DATA("CDA"),
	/** The SRV_DATA. */
	SRV_DATA("SDA"),
	/** The CLN_ECHO. */
	CLN_ECHO("CEC"),
	/** The SRV_ECHO. */
	SRV_ECHO("SEC"),
	/** The CLN_SYSTEM. */
	CLN_SYSTEM("CLN_SYSTEM"),
	/** The SRV_SYSTEM. */
	SRV_SYSTEM("SRV_SYSTEM"),

	/** The CLN_SUBSCRIBE. */
	CLN_SUBSCRIBE("CSU"),
	/** The SRV_SUBSCRIBE. */
	SRV_SUBSCRIBE("SSU"),
	/** The CLN_UNSUBSCRIBE. */
	CLN_UNSUBSCRIBE("CUN"),
	/** The SRV_UNSUBSCRIBE. */
	SRV_UNSUBSCRIBE("SUN"),
	/** The CLN_CHANGE_SUBSCRIPTION. */
	CLN_CHANGE_SUBSCRIPTION("CHS"),
	/** The SRV_CHANGE_SUBSCRIPTION. */
	SRV_CHANGE_SUBSCRIPTION("SHS"),
	/** The RECEIVE_PUBLICATION. */
	RECEIVE_PUBLICATION("CRP"),
	/** The PUBLISH. */
	PUBLISH("SPU"),
	/** The REGISTER_SERVICE. */
	REGISTER_SERVICE("REG"),
	/** The DEREGISTER_SERVICE. */
	DEREGISTER_SERVICE("DRG"),
	/** The UNDEFINED. */
	UNDEFINED("UND");

	/** The name. */
	private String name;
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

	/** {@inheritDoc} */
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

	@Override
	public String getValue() {
		return this.getName();
	}

	@Override
	public SCMPMsgType reverse(String messageId) {
		return reverseMap.get(messageId);
	}
}
