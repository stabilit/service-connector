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
package com.stabilit.scm.common.service;

import java.security.InvalidParameterException;

/**
 * The Class SCMessage. A SCMessage is the basic transport unit to communicate with a Service Connector.
 * 
 * @author JTraber
 */
public class SCMessage implements ISCMessage {

	/** The message info. */
	private String messageInfo;
	/** The compressed - regards data part of the message. */
	private boolean compressed;
	/** The data. */
	private Object data;
	/** The session id - identifies session context of communication. */
	private String sessionId;

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

	/** {@inheritDoc} */
	@Override
	public void setMessageInfo(String messageInfo) {
		if (messageInfo == null) {
			throw new InvalidParameterException("Message info must be set");
		}
		if (messageInfo.getBytes().length < 256) {
			throw new InvalidParameterException("Message info too long, over 256 bytes");
		}
		this.messageInfo = messageInfo;
	}

	/** {@inheritDoc} */
	@Override
	public String getMessageInfo() {
		return messageInfo;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCompressed() {
		return compressed;
	}

	/** {@inheritDoc} */
	@Override
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/** {@inheritDoc} */
	@Override
	public void setData(Object data) {
		this.data = data;
	}

	/** {@inheritDoc} */
	@Override
	public Object getData() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
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

	/** {@inheritDoc} */
	@Override
	public boolean isFault() {
		return false;
	}
}
