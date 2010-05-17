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
package com.stabilit.sc.listener;

import java.util.EventListener;

/**
 * The Class ConnectionListenerSupport. Allows logging on connection level - fire read/write, connect/disconnect.
 */
public final class ConnectionListenerSupport extends ListenerSupport<IConnectionListener> {

	/** The connection listener support. */
	private static ConnectionListenerSupport connectionListenerSupport = new ConnectionListenerSupport();

	/**
	 * Instantiates a new connection listener support.
	 */
	private ConnectionListenerSupport() {
	}

	/**
	 * Gets the single instance of ConnectionListenerSupport.
	 * 
	 * @return single instance of ConnectionListenerSupport
	 */
	public static ConnectionListenerSupport getInstance() {
		return connectionListenerSupport;
	}

	/**
	 * Fire connect.
	 * 
	 * @param source
	 *            the source
	 * @param port
	 *            the port
	 * @param data
	 *            the data
	 */
	public void fireConnect(Object source, int port) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, null);
			ConnectionListenerSupport.getInstance().fireConnect(connectionEvent);
		}
	}

	/**
	 * Fire disconnect.
	 * 
	 * @param source
	 *            the source
	 * @param port
	 *            the port
	 */
	public void fireDisconnect(Object source, int port) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, null);
			ConnectionListenerSupport.getInstance().fireDisconnect(connectionEvent);
		}
	}

	/**
	 * Fire write.
	 * 
	 * @param source
	 *            the source
	 * @param buffer
	 *            the buffer
	 * @param port
	 *            the port
	 */
	public void fireWrite(Object source, int port, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, buffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	/**
	 * Fire write.
	 * 
	 * @param source
	 *            the source
	 * @param buffer
	 *            the buffer
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param port
	 *            the port
	 */
	public void fireWrite(Object source, int port, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	/**
	 * Fire read.
	 * 
	 * @param source
	 *            the source
	 * @param buffer
	 *            the buffer
	 * @param port
	 *            the port
	 */
	public void fireRead(Object source, int port, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, buffer);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	/**
	 * Fire read.
	 * 
	 * @param source
	 *            the source
	 * @param buffer
	 *            the buffer
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param port
	 *            the port
	 */
	public void fireRead(Object source, int port, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, port, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	/**
	 * Fire connect.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 */
	public void fireConnect(ConnectionEvent connectionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IConnectionListener connectionListener = (IConnectionListener) localArray[i];
				connectionListener.connectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fire disconnect.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 */
	public void fireDisconnect(ConnectionEvent connectionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IConnectionListener connectionListener = (IConnectionListener) localArray[i];
				connectionListener.disconnectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fire write.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 */
	public void fireWrite(ConnectionEvent connectionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IConnectionListener connectionListener = (IConnectionListener) localArray[i];
				connectionListener.writeEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fire read.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 */
	public void fireRead(ConnectionEvent connectionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IConnectionListener connectionListener = (IConnectionListener) localArray[i];
				connectionListener.readEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
