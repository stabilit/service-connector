/*
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
 */
package org.serviceconnector.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;

// TODO: Auto-generated Javadoc
/**
 * The Class SCMPCacheMessage.
 */
public class SCCacheMessage implements Serializable {

	/** The header. */
	private Map<String, String> header;
	
	/** The body. */
	private Object body;
	
	/**
	 * Instantiates a new sCMP cache message.
	 *
	 * @param body the body
	 */
	public SCCacheMessage(Object body) {
		this.body = body;
		this.header = new HashMap<String, String>();
	}
	
	/**
	 * Sets the message type.
	 * 
	 * @param messageType
	 *            the new message type
	 */
	public void setMessageType(SCMPMsgType messageType) {
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageType.getValue());
	}

	/**
	 * Sets the message type.
	 * 
	 * @param messageTypeValue
	 *            the new message type
	 */
	public void setMessageType(String messageTypeValue) {
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, messageTypeValue);
	}

	/**
	 * Returns the value of the header attribute.
	 * 
	 * @param headerType
	 *            the header type
	 * @return the attribute value
	 */
	public String getHeader(SCMPHeaderAttributeKey headerType) {
		return this.header.get(headerType.getValue());
	}

	/**
	 * Sets the header attribute by type and value.
	 * 
	 * @param headerType
	 *            the header type
	 * @param attributeValue
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey headerType, String attributeValue) {
		this.header.put(headerType.getValue(), attributeValue);
	}


	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}
	
	/**
	 * Sets the body.
	 *
	 * @param body the new body
	 */
	public void setBody(Object body) {
		this.body = body;
	}
}
