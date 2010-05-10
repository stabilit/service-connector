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
package com.stabilit.sc.cln.client;

import com.stabilit.sc.net.IConnection;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.SCMP;

/**
 * The Interface IClientConnection abstracts any connection to a server.
 * 
 * @author JTraber
 */
public interface IClientConnection extends IConnection {

	/**
	 * Connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	void connect() throws Exception;

	/**
	 * Send and receive synchronous operation.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the scmp
	 * @throws Exception
	 *             the exception
	 */
	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	/**
	 * Disconnect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void disconnect() throws Exception;

	/**
	 * Destroys connection.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void destroy() throws Exception;

	/**
	 * Sets the encoder decoder.
	 * 
	 * @param encoderDecoder
	 *            the new encoder decoder
	 */
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder);
}
