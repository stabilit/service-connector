/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.sc.net.netty.tcp;

/**
 * The Class SCMPDecoderException. Occurs when decoding SCMP frame fails.
 * 
 * @author JTraber
 */
public class SCMPDecoderException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6537338790870840933L;

	/**
	 * Instantiates a new sCMP decoder exception.
	 */
	public SCMPDecoderException() {
		super();
	}

	/**
	 * Instantiates a new sCMP decoder exception.
	 * 
	 * @param msg
	 *            the msg
	 */
	public SCMPDecoderException(String msg) {
		super(msg);
	}
}
