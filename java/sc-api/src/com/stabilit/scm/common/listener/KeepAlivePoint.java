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
package com.stabilit.scm.common.listener;

import java.util.EventListener;

import com.stabilit.scm.common.net.req.IConnection;

/**
 * The Class KeepAlivePoint. Allows listening for keep alive events - fire keep
 * alive
 */
public final class KeepAlivePoint extends ListenerSupport<IKeepAliveListener> {

	/** The connection point. */
	private static KeepAlivePoint keepAlive = new KeepAlivePoint();

	/**
	 * Instantiates a new connection point.
	 */
	private KeepAlivePoint() {
	}

	/**
	 * Gets the single instance of ConnectionPoint.
	 * 
	 * @return single instance of ConnectionPoint
	 */
	public static KeepAlivePoint getInstance() {
		return keepAlive;
	}

	/**
	 * Fire keep alive for given connection
	 * 
	 * @param source
	 *            the source
	 * @param connection
	 *            the connection instance where keep alive is required
	 */
	public void fireKeepAlive(Object source, IConnection connection) {
		if (getInstance().isEmpty() == false) {
			KeepAliveEvent keepAliveEvent = new KeepAliveEvent(source,
					connection);
			KeepAlivePoint.getInstance().fireKeepAlive(keepAliveEvent);
		}
	}

	/**
	 * Fire keep alive
	 * 
	 * @param keepAliveEvent
	 *            the keep alive event
	 */
	public void fireKeepAlive(KeepAliveEvent keepAliveEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IKeepAliveListener keepAliveListener = (IKeepAliveListener) localArray[i];
				keepAliveListener.keepAliveEvent(keepAliveEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
