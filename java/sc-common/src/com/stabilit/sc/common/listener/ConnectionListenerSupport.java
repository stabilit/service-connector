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
package com.stabilit.sc.common.listener;

import java.util.Collections;
import java.util.Iterator;

public class ConnectionListenerSupport extends ListenerSupport<IConnectionListener> {

	private static ConnectionListenerSupport connectionListenerSupport = new ConnectionListenerSupport();

	private ConnectionListenerSupport() {
	}

	public static ConnectionListenerSupport getInstance() {
		return connectionListenerSupport;
	}

	public void fireConnect(Object source) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, null);
			ConnectionListenerSupport.getInstance().fireConnect(connectionEvent);
		}
	}

	public void fireDisconnect(Object source) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, null);
			ConnectionListenerSupport.getInstance().fireDisconnect(connectionEvent);
		}
	}

	public static void fireWrite(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireWrite(Object source, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public void fireConnect(ConnectionEvent connectionEvent) {
		Iterator<IConnectionListener> iter = null;
		synchronized (this) {
			if (this.listenerList == this.unmodifiableList) {
				this.unmodifiableList = Collections.unmodifiableList(this.listenerList);
			}
			iter = unmodifiableList.iterator();
		}
		while (iter.hasNext()) {
			try {
				IConnectionListener connectionListener = iter.next();
				connectionListener.connectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireDisconnect(ConnectionEvent connectionEvent) {
		Iterator<IConnectionListener> iter = null;
		synchronized (this) {
			if (this.listenerList == this.unmodifiableList) {
				this.unmodifiableList = Collections.unmodifiableList(this.listenerList);
			}
			iter = unmodifiableList.iterator();
		}
		while (iter.hasNext()) {
			try {
				IConnectionListener connectionListener = iter.next();
				connectionListener.disconnectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireWrite(ConnectionEvent connectionEvent) {
		Iterator<IConnectionListener> iter = null;
		synchronized (this) {
			if (this.listenerList == this.unmodifiableList) {
				this.unmodifiableList = Collections.unmodifiableList(this.listenerList);
			}
			iter = unmodifiableList.iterator();
		}
		while (iter.hasNext()) {
			try {
				IConnectionListener connectionListener = iter.next();
				connectionListener.writeEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireRead(ConnectionEvent connectionEvent) {
		Iterator<IConnectionListener> iter = null;
		synchronized (this) {
			if (this.listenerList == this.unmodifiableList) {
				this.unmodifiableList = Collections.unmodifiableList(this.listenerList);
			}
			iter = unmodifiableList.iterator();
		}
		while (iter.hasNext()) {
			try {
				IConnectionListener connectionListener = iter.next();
				connectionListener.readEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
