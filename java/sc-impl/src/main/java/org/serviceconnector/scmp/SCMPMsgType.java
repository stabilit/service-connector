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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;
import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;


/**
 * The Enum SCMPMsgType. Defines possible message types in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPMsgType implements IReversibleEnum<String, SCMPMsgType> {

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
	/** The REGISTER_SERVER. */
	REGISTER_SERVER("REG"),
	/** The DEREGISTER_SERVER. */
	DEREGISTER_SERVER("DRG"),
	/** The CLN_EXECUTE. */
	CLN_EXECUTE("CXE"),
	/** The SRV_EXECUTE. */
	SRV_EXECUTE("SXE"),
	/** The ECHO. */
	ECHO("CEC"),

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

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPMsgType.class);

	/** The value. */
	private String value;
	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, SCMPMsgType> reverseMap = new ReverseEnumMap<String, SCMPMsgType>(
			SCMPMsgType.class);

	/**
	 * Instantiates a SCMPMsgType.
	 * 
	 * @param value
	 *            the value
	 */
	private SCMPMsgType(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * Gets the msg type by string.
	 * 
	 * @param messageId
	 *            the message id
	 * @return the msg type
	 */
	public static SCMPMsgType getMsgType(String messageId) {
		SCMPMsgType msgType = reverseMap.get(messageId);
		if (msgType == null) {
			// messageId doesn't match to a valid SCMPMsgType
			return SCMPMsgType.UNDEFINED;
		}
		return msgType;
	}

	@Override
	public SCMPMsgType reverse(String messageId) {
		return SCMPMsgType.getMsgType(messageId);
	}

	@Override
	public String toString() {
		return this.value;
	}
}
