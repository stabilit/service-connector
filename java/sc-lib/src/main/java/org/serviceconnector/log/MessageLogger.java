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
package org.serviceconnector.log;

import java.util.Formatter;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class MessageLogger.
 */
public final class MessageLogger {

	/** The Constant messageLogger. */
	private static final Logger MESSAGE_LOGGER = Logger.getLogger(Loggers.MESSAGE.getValue());
	/** The MSG_INPUT_STR. */
	private static String msgInputStr = "<-%s %s";
	/** The MSG_OUTPUT_STR. */
	private static String msgOutputStr = "->%s %s";

	/**
	 * Private constructor for singleton use.
	 */
	private MessageLogger() {
	}

	/**
	 * produce the log for an input message.
	 * 
	 * @param headlineKey
	 *            the headline key
	 * @param message
	 *            the message
	 */
	public static synchronized void logInputMessage(SCMPHeaderKey headlineKey, SCMPMessage message) {
		if (MESSAGE_LOGGER.isTraceEnabled()) {
			// write out all header attributes
			Formatter format = new Formatter();
			format.format(msgInputStr, headlineKey.toString(), message.getHeader().toString());
			MESSAGE_LOGGER.trace(format.toString());
			format.close();
		} else if (MESSAGE_LOGGER.isDebugEnabled()) {
			// write out only important attributes
			StringBuilder builder = new StringBuilder();
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MSG_TYPE, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SERVICE_NAME, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SESSION_ID, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MASK, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.NO_DATA, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CASCADED_MASK, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CACHE_ID, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT, message));
			Formatter format = new Formatter();
			format.format(msgInputStr, headlineKey.toString(), builder.toString());
			MESSAGE_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * produce the log for an output message.
	 * 
	 * @param headlineKey
	 *            the headline key
	 * @param message
	 *            the message
	 */
	public static synchronized void logOutputMessage(SCMPHeaderKey headlineKey, SCMPMessage message) {
		if (MESSAGE_LOGGER.isTraceEnabled()) {
			// write out all header attributes
			Formatter format = new Formatter();
			format.format(msgOutputStr, headlineKey.toString(), message.getHeader().toString());
			MESSAGE_LOGGER.trace(format.toString());
			format.close();
		} else if (MESSAGE_LOGGER.isDebugEnabled()) {
			// write out only important attributes
			StringBuilder builder = new StringBuilder();
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MSG_TYPE, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SERVICE_NAME, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SESSION_ID, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MASK, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.NO_DATA, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CASCADED_MASK, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CACHE_ID, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE, message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT, message));
			Formatter format = new Formatter();
			format.format(msgOutputStr, headlineKey.toString(), builder.toString());
			MESSAGE_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Format header attribute for output in the log.
	 * 
	 * @param key
	 *            the header attribute key e.g. MTY
	 * @param message
	 *            SCMP message
	 * @return the string
	 *         string like MTY=REG or "" if attribute is missing
	 */
	private static String formatAttribute(SCMPHeaderAttributeKey key, SCMPMessage message) {
		String attrValue = message.getHeader(key);
		if (attrValue == null || attrValue.equals("")) {
			return "";
		} else {
			return " " + key.getValue() + "=" + attrValue;
		}
	}

	/**
	 * Checks if is log enabled.
	 * 
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return MESSAGE_LOGGER.isDebugEnabled();
	}
}
