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
package org.serviceconnector.service;

/**
 * The Interface ISCMessage. ISCMessage represents basic message interface for a request/ response to/from a SC.
 * 
 * @author JTraber
 */
public interface ISCMessage {

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            Optional information passed together with the message body that helps to identify the message content
	 *            without investigating the body.<br>
	 *            Any printable character, length > 0 and < 256 Byte<br>
	 *            Example: SECURITY_MARKET_QUERY
	 */
	public abstract void setMessageInfo(String messageInfo);

	/**
	 * Gets the message info.
	 * 
	 * @return the message info
	 */
	public abstract String getMessageInfo();

	/**
	 * Checks if is compressed.
	 * 
	 * @return the boolean
	 */
	public abstract boolean isCompressed();

	/**
	 * Sets the compressed. Default is true.
	 * 
	 * @param compressed
	 *            Regards the data part of the message.
	 */
	public abstract void setCompressed(boolean compressed);

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public abstract Object getData();

	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the data
	 */
	public abstract void setData(Object data);

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public abstract String getSessionId();

	/**
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	public abstract boolean isFault();
}
