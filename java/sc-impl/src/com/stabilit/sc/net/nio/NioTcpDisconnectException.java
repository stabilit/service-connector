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
package com.stabilit.sc.net.nio;

/**
 * The Class NioTcpDisconnectException. Exception occurs when server is i reading on a socket and client
 * disconnects.
 * 
 * @author JTraber
 */
public class NioTcpDisconnectException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2863411772880184904L;

	/**
	 * Instantiates a new nio tcp disconnect exception.
	 */
	public NioTcpDisconnectException() {
		super();
	}

	/**
	 * Instantiates a new nio tcp disconnect exception.
	 * 
	 * @param msg the msg
	 */
	public NioTcpDisconnectException(String msg) {
		super(msg);
	}
}
