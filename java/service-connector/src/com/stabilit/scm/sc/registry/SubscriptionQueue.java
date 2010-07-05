/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.sc.registry;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTraber
 */
public class SubscriptionQueue {

	private List<DataItem> dataQueue;
	private Map<String, SessionItem> sessionQueueMap;

	public SubscriptionQueue() {
		this.dataQueue = Collections.synchronizedList(new LinkedList<DataItem>());
		this.sessionQueueMap = new ConcurrentHashMap<String, SessionItem>();

	}

	public boolean hasNext(String sessionId, String mask) {
		SessionItem item = this.sessionQueueMap.get(sessionId);
		if (item == null) {
		    return false;
		}
		if (item.hasNext() == false) {
			return false;
		}
		return true;
	}

	public Object poll(String sessionId, String mask) {
		SessionItem item = this.sessionQueueMap.get(sessionId);
		if (item == null) {
			return null;
		}
		return item.getNext();
	}

	class SessionItem {
		private int index;

		public SessionItem() {
			index = -1;
		}
		
		public boolean hasNext() {
			return index >= 0 && index < SubscriptionQueue.this.dataQueue.size();
		}
		
		public Object getNext() {
			DataItem dataItem = SubscriptionQueue.this.dataQueue.get(index++);
			if (index >= SubscriptionQueue.this.dataQueue.size()) {
				index = -1;
			}
			dataItem.referenced--;
			return dataItem.obj;
		}
	}
	
	class DataItem {
		private int referenced;
		private Object obj;
		
		public DataItem() {
			this.referenced = 0;
			this.obj = null;
		}
	}

}
