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
 * The Class EncodingDecodingException. Occurs when encoding/decoding fails.
 * 
 * @author JTraber
 */
public class EncodingDecodingException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5212244086716544669L;

	/**
	 * Instantiates a new encoding decoding exception.
	 * 
	 * @param message
	 *            the message
	 */
	public EncodingDecodingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new encoding decoding exception.
	 * 
	 * @param exception
	 *            the cause
	 */
	public EncodingDecodingException(Exception exception) {
		super(exception);
	}

	/**
	 * Instantiates a new encoding decoding exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public EncodingDecodingException(String message, Exception cause) {
		super(message, cause);
	}
}
