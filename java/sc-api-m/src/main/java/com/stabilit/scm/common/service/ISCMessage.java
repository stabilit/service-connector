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
	 *            the new message info
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
	public abstract Boolean isCompressed();

	/**
	 * Sets the compressed.
	 * 
	 * @param compressed
	 *            the new compressed
	 */
	public abstract void setCompressed(Boolean compressed);

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
	 *            the new data
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
