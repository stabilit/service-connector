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
	private static final MessageLogger instance = new MessageLogger();

	private String MSG_LONG_STR = "msg:%s";
	private String MSG_SHORT_STR = "msg:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private MessageLogger() {
	}

	public static MessageLogger getInstance() {
		return MessageLogger.instance;
	}

	/**
	 * @param className
	 * @param message
	 */
	public synchronized void logMessage(String className, SCMPMessage message) {
		if (messageLogger.isTraceEnabled()) {
			// write out all header attributes
			Formatter format = new Formatter();
			format.format(MSG_LONG_STR, message.getHeader().toString());
			messageLogger.trace(format.toString());
			format.close();
		} else if (messageLogger.isDebugEnabled()) {
			// write out only important attributes
			StringBuilder builder = new StringBuilder();
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.MSG_TYPE,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.SERVICE_NAME,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.SESSION_ID,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.MASK,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.IP_ADDRESS_LIST,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.CASCADED_MASK,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.CACHE_ID,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE,message));
			builder.append(this.formatAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT,message));		
			Formatter format = new Formatter();
			format.format(MSG_SHORT_STR, builder.toString());
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
	private String formatAttribute(SCMPHeaderAttributeKey key, SCMPMessage message) {
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
	public boolean isEnabled() {
		return messageLogger.isDebugEnabled();
	}
}
