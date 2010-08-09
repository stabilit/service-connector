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

public final class SubscriptionPoint extends ListenerSupport<IConnectionListener> {

	private static SubscriptionPoint subscriptionPoint = new SubscriptionPoint();

	private SubscriptionPoint() {
	}

	public static SubscriptionPoint getInstance() {
		return subscriptionPoint;
	}

	public void fireSubscriptionNoDataTimeout(Object source, String sessionId) {
		if (getInstance().isEmpty() == false) {
			SubscriptionEvent connectionEvent = new SubscriptionEvent(source, sessionId);
			SubscriptionPoint.getInstance().fireNoDataTimeout(connectionEvent);
		}
	}

	public void firePoll(Object source, String sessionId, Object queueItem) {
		if (getInstance().isEmpty() == false) {
			if (queueItem instanceof SCMPMessage) {
				SubscriptionEvent connectionEvent = new SubscriptionEvent(source, sessionId,
						(SCMPMessage) queueItem);
				SubscriptionPoint.getInstance().fireNoDataTimeout(connectionEvent);
			}
		}
	}

	public void fireNoDataTimeout(SubscriptionEvent connectionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				ISubscriptionListener connectionListener = (ISubscriptionListener) localArray[i];
				connectionListener.noDataTimeoutEvent(connectionEvent);
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}
}
