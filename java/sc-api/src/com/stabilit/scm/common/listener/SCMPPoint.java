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

import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SCMPPoint. Allows notifying scmp information.
 */
public final class SCMPPoint extends ListenerSupport<ISCMPListener> {

	/** The scmp point. */
	private static SCMPPoint scmpPoint = new SCMPPoint();

	/**
	 * Instantiates a new SCMPPoint.
	 */
	private SCMPPoint() {
	}

	/**
	 * Gets the single instance of SCMPPoint.
	 * 
	 * @return single instance of SCMPPoint
	 */
	public static SCMPPoint getInstance() {
		return scmpPoint;
	}

	public void fireEncode(Object source, SCMPMessage scmp) {
		if (getInstance().isEmpty() == false) {
			SCMPEvent scmpEvent = new SCMPEvent(source, scmp);
			SCMPPoint.getInstance().fireEncode(scmpEvent);
		}
	}

	/**
	 * Fire scmp encode event.
	 * 
	 * @param scmpEvent
	 *            the scmp event
	 */
	public void fireEncode(SCMPEvent scmpEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISCMPListener scmpListener = (ISCMPListener) localArray[i];
				scmpListener.encodeEvent(scmpEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireDecode(Object source, SCMPMessage scmp) {
		if (getInstance().isEmpty() == false) {
			SCMPEvent scmpEvent = new SCMPEvent(source, scmp);
			SCMPPoint.getInstance().fireDecode(scmpEvent);
		}
	}

	/**
	 * Fire scmp encode event.
	 * 
	 * @param scmpEvent
	 *            the scmp event
	 */
	public void fireDecode(SCMPEvent scmpEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISCMPListener scmpListener = (ISCMPListener) localArray[i];
				scmpListener.decodeEvent(scmpEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
