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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.IRequestResponse;

/**
 * @author JTraber
 */
public class SubscriptionQueue {

	private Timer timer;
	private List<DataItem> dataQueue;
	private Map<String, DataPointer> dataPointerMap;

	public SubscriptionQueue() {
		this.dataQueue = Collections.synchronizedList(new LinkedList<DataItem>());
		this.dataPointerMap = new ConcurrentHashMap<String, DataPointer>();
		this.timer = new Timer("SubscriptionQueue");
	}

	public void add(SCMPMessage message) {
		DataItem dataItem = new DataItem(message);
		dataQueue.add(dataItem);
		fireNewDataArrived(dataQueue.size() - 1);
	}

	public boolean hasNext(String sessionId, String mask) {
		DataPointer item = this.dataPointerMap.get(sessionId);
		if (item == null) {
			return false;
		}
		if (item.hasNext() == false) {
			return false;
		}
		return true;
	}

	public Object poll(String sessionId, String mask) {
		DataPointer item = this.dataPointerMap.get(sessionId);
		if (item == null) {
			return null;
		}
		Object obj = item.getNext(); // cancel timer inside
		return obj;
	}

	private void fireNewDataArrived(int pointerIndex) {
		Object[] dataPointerArray = null;
		synchronized (this.dataPointerMap) {
			dataPointerArray = this.dataPointerMap.entrySet().toArray();
		}
		for (int i = 0; i < dataPointerArray.length; i++) {
			Entry entry = (Entry) dataPointerArray[i];
			DataPointer dataPointer = (DataPointer) entry.getValue();
			if (dataPointer.index == pointerIndex) {
				dataPointer.taskItem.schedule(0);
			}

		}
	}

	class DataPointer {
		private int index;
		private TaskItem taskItem;

		public DataPointer() {
			this(null);
		}

		public DataPointer(TimerTask timerTask) {
			this.index = 0;
			this.taskItem = new TaskItem(timerTask);
		}

		public boolean hasNext() {
			return index >= 0 && index < SubscriptionQueue.this.dataQueue.size();
		}

		public Object getNext() {
			DataItem dataItem = SubscriptionQueue.this.dataQueue.get(index++);
			if (index >= SubscriptionQueue.this.dataQueue.size()) {
				index = -1;
			}
			taskItem.cancel();
			dataItem.referenced--;
			return dataItem.obj;
		}
	}

	class DataItem {
		private int referenced;
		private Object obj;

		public DataItem(Object obj) {
			this.referenced = 0;
			this.obj = obj;
		}
	}

	class TaskItem {
		private TimerTask task;
		private boolean cancelled = true;

		public TaskItem(TimerTask task) {
			this.task = task;
		}

		public void cancel() {
			try {
				if (this.cancelled == false) {
					return;
				}
				this.cancelled = true;
				this.task.cancel();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		public void schedule(int time) {
			try {
				if (this.cancelled == false) {
					this.cancel();
				}
				this.cancelled = false;
				timer.schedule(this.task, time);
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}

	public void listen(String sessionId, IRequest request, IResponse response) {
		DataPointer dataPointer = dataPointerMap.get(sessionId);
		if (dataPointer != null) {
			((IRequestResponse)dataPointer.taskItem.task).setRequest(request);
			((IRequestResponse)dataPointer.taskItem.task).setResponse(response);
		}
		
	}

	public void subscribe(String sessionId, TimerTask timerTask) {
		DataPointer dataPointer = new DataPointer(timerTask);
		dataPointerMap.put(sessionId, dataPointer);
	}

	public void unsubscribe(String sessionId) {
		DataPointer dataPointer = dataPointerMap.get(sessionId);
		if (dataPointer != null) {
			dataPointer.taskItem.cancel();
			dataPointerMap.remove(sessionId);
		}
	}
}
