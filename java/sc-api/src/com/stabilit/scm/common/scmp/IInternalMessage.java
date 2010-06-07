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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The Interface IInternalMessage. Abstracts internal message. Internal Messages are used to communicate for
 * testing / maintaining reasons.
 */
public interface IInternalMessage {

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public SCMPMsgType getKey();

	/**
	 * New instance.
	 * 
	 * @return the internal message
	 */
	public IInternalMessage newInstance();

	/**
	 * Gets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @return the attribute
	 */
	public Object getAttribute(String name);

	/**
	 * Sets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setAttribute(String name, Object value);

	/**
	 * Gets the attribute map.
	 * 
	 * @return the attribute map
	 */
	Map<String, Object> getAttributeMap();

	/**
	 * Encode.
	 * 
	 * @param bw
	 *            the bw
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void encode(BufferedWriter bw) throws IOException;

	/**
	 * Decode.
	 * 
	 * @param br
	 *            the br
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void decode(BufferedReader br) throws IOException;

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength();
}
