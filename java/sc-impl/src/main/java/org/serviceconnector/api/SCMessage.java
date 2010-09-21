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
package org.serviceconnector.api;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;


/**
 * The Class SCMessage. A SCMessage is the basic transport unit to communicate with a Service Connector.
 * 
 * @author JTraber
 */
public class SCMessage{

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMessage.class);
	
	/** The message info. */
	private String messageInfo;
	/** The compressed - regards data part of the message. */
	private boolean compressed;
	/** The data. */
	private Object data;
	/** The session id - identifies session context of communication. */
	private String sessionId;
	/** The operation timeout in milliseconds - time to execute */
	private int operationTimeout;
	
	/**
	 * Instantiates a new SCMessage.
	 */
	public SCMessage() {
		this.messageInfo = null;
		// default of compression is true
		this.compressed = true;
		this.data = null;
		this.sessionId = null;
	}

	/**
	 * Instantiates a new SCMessage.
	 * 
	 * @param data
	 *            the data
	 */
	public SCMessage(Object data) {
		this();
		this.data = data;
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            Optional information passed together with the message body that helps to identify the message content
	 *            without investigating the body.<br>
	 *            Any printable character, length > 0 and < 256 Byte<br>
	 *            Example: SECURITY_MARKET_QUERY
	 */
	public void setMessageInfo(String messageInfo) {
		if (messageInfo != null) {
			int messageInfoLength = messageInfo.getBytes().length;
			if (messageInfoLength < 1 || messageInfoLength > 256) {
				throw new InvalidParameterException("Message info not within 1 to 256 bytes.");
			}
		}
		this.messageInfo = messageInfo;
	}

	/**
	 * Gets the message info.
	 * 
	 * @return the message info
	 */
	public String getMessageInfo() {
		return messageInfo;
	}

	/**
	 * Checks if is compressed.
	 * 
	 * @return the boolean
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * Sets the compressed. Default is true.
	 * 
	 * @param compressed
	 *            Regards the data part of the message.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the data
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Object getData() {
		return this.data;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		return false;
	}
	
	/**
	 * Gets the operation timeout, equals to the time accepted for request execution
	 * @return
	 */
	public int getOperationTimeout() {
		return operationTimeout;
	}

	public void setOperationTimeout(int operationTimeout) {
		this.operationTimeout = operationTimeout;
	}
}
