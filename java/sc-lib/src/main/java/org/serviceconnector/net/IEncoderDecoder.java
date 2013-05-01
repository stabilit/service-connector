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

import java.io.InputStream;
import java.io.OutputStream;


/**
 * The Interface IEncoderDecoder. Abstracts EncoderDecoder implementations.
 */
public interface IEncoderDecoder {

	/**
	 * Encode object to output stream.
	 * 
	 * @param os
	 *            the os to fill
	 * @param obj
	 *            the obj to encode
	 * @throws Exception
	 *             the exception
	 */
	public void encode(OutputStream os, Object obj) throws Exception;

	/**
	 * Decode input stream.
	 * 
	 * @param is
	 *            the is to decode
	 * @return the object decoded
	 * @throws Exception
	 *             the exception
	 */
	public Object decode(InputStream is) throws Exception;
}
