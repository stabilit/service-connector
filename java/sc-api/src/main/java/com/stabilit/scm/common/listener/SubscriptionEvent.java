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

import java.util.EventObject;

import com.stabilit.scm.common.scmp.SCMPMessage;

public class SubscriptionEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8265225164155917995L;

	private String sessionId;
	private SCMPMessage queueItem;
	private int queueSize = -1;

	public SubscriptionEvent(Object source, int queueSize) {
		this(source, null, null, queueSize);
	}

	public SubscriptionEvent(Object source, String sessionId) {
		this(source, sessionId, null, -1);
	}

	public SubscriptionEvent(Object source, SCMPMessage queueItem, int queueSize) {
		this(source, null, queueItem, queueSize);
	}

	public SubscriptionEvent(Object source, String sessionId,
			SCMPMessage queueItem, int queueSize) {
		super(source);
		this.sessionId = sessionId;
		this.queueItem = queueItem;
		this.queueSize = queueSize;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public SCMPMessage getQueueItem() {
		return this.queueItem;
	}

	public int getQueueSize() {
		return this.queueSize;
	}
}
