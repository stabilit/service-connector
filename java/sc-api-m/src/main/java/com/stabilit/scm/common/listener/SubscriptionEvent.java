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

	public SubscriptionEvent(Object source, String sessionId) {
		this(source, sessionId, null);
	}

	public SubscriptionEvent(Object source, String sessionId, SCMPMessage queueItem) {
		super(source);
		this.sessionId = sessionId;
		this.queueItem = queueItem;
	}

	public String getSessionId() {
		return sessionId;
	}

	public SCMPMessage getQueueItem() {
		return queueItem;
	}
}
