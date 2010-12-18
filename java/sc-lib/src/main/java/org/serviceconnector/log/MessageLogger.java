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
import org.serviceconnector.scmp.SCMPMessage;


public class MessageLogger {

	private static final Logger messageLogger = Logger.getLogger(Loggers.MESSAGE.getValue());

	private static String MSG_INPUT_STR = "<-:%s";
	private static String MSG_OUTPUT_STR = "->:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private MessageLogger() {
	}

	/**
	 * @param className
	 * @param message
	 */
	public static synchronized void logInputMessage(String className, SCMPMessage message) {
		if (messageLogger.isTraceEnabled()) {
			// write out all header attributes
			Formatter format = new Formatter();
			format.format(MSG_INPUT_STR, message.getHeader().toString());
			messageLogger.trace(format.toString());
			format.close();
		} else if (messageLogger.isDebugEnabled()) {
			// write out only important attributes
			StringBuilder builder = new StringBuilder();
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MSG_TYPE,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SERVICE_NAME,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SESSION_ID,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MASK,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.IP_ADDRESS_LIST,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CASCADED_MASK,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CACHE_ID,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT,message));		
			Formatter format = new Formatter();
			format.format(MSG_INPUT_STR, builder.toString());
			messageLogger.debug(format.toString());
			format.close();
		}
	}

	/**
	 * @param className
	 * @param message
	 */
	public static synchronized void logOutputMessage(String className, SCMPMessage message) {
		if (messageLogger.isTraceEnabled()) {
			// write out all header attributes
			Formatter format = new Formatter();
			format.format(MSG_OUTPUT_STR, message.getHeader().toString());
			messageLogger.trace(format.toString());
			format.close();
		} else if (messageLogger.isDebugEnabled()) {
			// write out only important attributes
			StringBuilder builder = new StringBuilder();
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MSG_TYPE,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SERVICE_NAME,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SESSION_ID,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.MASK,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.IP_ADDRESS_LIST,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CASCADED_MASK,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.CACHE_ID,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE,message));
			builder.append(MessageLogger.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT,message));		
			Formatter format = new Formatter();
			format.format(MSG_OUTPUT_STR, builder.toString());
			messageLogger.debug(format.toString());
			format.close();
		}
	}

	/**
	 * @param key
	 * 				e.g. MTY
	 * @param message
	 * 				SCMP message
	 * @return
	 * 		string like MTY=REG or "" if attribute is missing
	 */
	private static String formatAttribute(SCMPHeaderAttributeKey key, SCMPMessage message) {
		String attrValue = message.getHeader(key);
		if (attrValue == null || attrValue.equals("")) {
			return "";
		}
		else {
			return " "+key.getValue()+"="+attrValue;
		}
	}

	
	/**
	 * @return
	 */
	public static boolean isEnabled() {
		return messageLogger.isDebugEnabled();
	}
}
