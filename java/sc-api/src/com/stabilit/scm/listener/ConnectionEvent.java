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
package com.stabilit.sc.listener;

import java.util.EventObject;

/**
 * The Class ConnectionEvent. Event for logging connection purpose.
 */
public class ConnectionEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8265225164155917995L;

	/** The offset. */
	private int offset;
	/** The length. */
	private int length;
	/** The data. */
	private Object data;
	/** The port. */
	private int port;

	/**
	 * Instantiates a new connection event.
	 * 
	 * @param source
	 *            the source
	 * @param data
	 *            the data
	 * @param port
	 *            the port of connection
	 */
	public ConnectionEvent(Object source, int port, Object data) {
		this(source, port, data, -1, -1);
	}

	/**
	 * Instantiates a new connection event.
	 * 
	 * @param source
	 *            the source
	 * @param data
	 *            the data
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param port
	 *            the port
	 */
	public ConnectionEvent(Object source, int port, Object data, int offset, int length) {
		super(source);
		this.offset = offset;
		this.length = length;
		this.data = data;
		this.port = port;		
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Gets the offset.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
}
