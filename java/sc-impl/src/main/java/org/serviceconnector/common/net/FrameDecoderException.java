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
package org.serviceconnector.common.net;

/**
 * The Class FrameDecoderException. Occurs when decoding frame fails.
 * 
 * @author JTraber
 */
public class FrameDecoderException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4215165615985380374L;

	/**
	 * Instantiates a new frame decoder exception.
	 * 
	 * @param message
	 *            the message
	 */
	public FrameDecoderException(String message) {
		super(message);
	}
}
