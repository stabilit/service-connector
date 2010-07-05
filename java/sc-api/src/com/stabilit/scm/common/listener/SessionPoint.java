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

public final class SessionPoint extends ListenerSupport<ISessionListener> {

	private static SessionPoint sessionPoint = new SessionPoint();

	private SessionPoint() {
	}

	public static SessionPoint getInstance() {
		return sessionPoint;
	}

	public void fireCreate(Object source, String sessionId) {
		if (getInstance().isEmpty() == false) {
			SessionEvent sessionEvent = new SessionEvent(source, sessionId);
			SessionPoint.getInstance().fireCreate(sessionEvent);
		}
	}

	public void fireDelete(Object source, String sessionId) {
		if (getInstance().isEmpty() == false) {
			SessionEvent sessionEvent = new SessionEvent(source, sessionId);
			SessionPoint.getInstance().fireDelete(sessionEvent);
		}
	}

	public void fireAbort(Object source, String sessionId) {
		if (getInstance().isEmpty() == false) {
			SessionEvent sessionEvent = new SessionEvent(source, sessionId);
			SessionPoint.getInstance().fireAbort(sessionEvent);
		}
	}

	public void fireCreate(SessionEvent sessionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISessionListener sessionListener = (ISessionListener) localArray[i];
				sessionListener.createSessionEvent(sessionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireDelete(SessionEvent sessionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISessionListener sessionListener = (ISessionListener) localArray[i];
				sessionListener.deleteSessionEvent(sessionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void fireAbort(SessionEvent sessionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISessionListener sessionListener = (ISessionListener) localArray[i];
				sessionListener.abortSessionEvent(sessionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}