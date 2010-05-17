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

import java.util.EventListener;

/**
 * The Class RuntimePoint.
 */
public final class RuntimePoint extends ListenerSupport<IRuntimeListener> {

	/** The runtime point. */
	private static RuntimePoint runtimePoint = new RuntimePoint();

	/**
	 * Instantiates a new RuntimePoint.
	 */
	private RuntimePoint() {
	}

	/**
	 * Gets the single instance of RuntimePoint.
	 * 
	 * @return single instance of RuntimePoint
	 */
	public static RuntimePoint getInstance() {
		return runtimePoint;
	}

	/**
	 * Fire runtime.
	 * 
	 * @param source
	 *            the source
	 * @param text
	 *            the text
	 */
	public void fireRuntime(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			RuntimeEvent runtimeEvent = new RuntimeEvent(source, text);
			RuntimePoint.getInstance().fireRuntime(runtimeEvent);
		}
	}

	/**
	 * Fire runtime.
	 * 
	 * @param runtimeEvent
	 *            the runtime event
	 */
	public void fireRuntime(RuntimeEvent runtimeEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IRuntimeListener runtimeListener = (IRuntimeListener) localArray[i];
				runtimeListener.runtimeEvent(runtimeEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
