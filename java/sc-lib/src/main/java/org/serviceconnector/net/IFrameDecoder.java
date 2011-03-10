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
package org.serviceconnector.net;


/**
 * The Interface IFrameDecoder. Abstracts FrameDecoder implementations.
 * 
 * @author JTraber
 */
public interface IFrameDecoder {

	/**
	 * Parses the frame size.
	 * 
	 * @param buffer
	 *            the buffer to parse
	 * @return the frame size
	 * @throws Exception
	 *             the exception
	 */
	public int parseFrameSize(byte[] buffer) throws Exception;

	/**
	 * Parses the message size.
	 * 
	 * @param buffer
	 *            the buffer
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public int parseMessageSize(byte[] buffer) throws Exception;
	
	/**
	 * Parses the header size.
	 * 
	 * @param buffer
	 *            the buffer
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public int parseHeaderSize(byte[] buffer) throws Exception;
}
