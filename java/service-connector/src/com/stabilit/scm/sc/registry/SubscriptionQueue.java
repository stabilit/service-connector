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
import com.stabilit.scm.common.util.LinkedQueue;
import com.stabilit.scm.common.util.LinkedQueue.LinkedNode;

/**
 * @author JTraber
 */
public class SubscriptionQueue {

	private Timer timer;
	private LinkedQueue<DataEntry> dataQueue; // the queue
	private Map<String, DataPointer> nodeMap; // maps session id to data pointer and its node in queue

	public SubscriptionQueue() {
		this.dataQueue = new LinkedQueue<DataEntry>();
		this.nodeMap = new ConcurrentHashMap<String, DataPointer>();
		this.timer = new Timer("SubscriptionQueue");
	}

	public void add(SCMPMessage message) {
		DataEntry dataEntry = new DataEntry(message);
		try {
			dataQueue.put(dataEntry);
			fireNewDataArrived();
			removeNonreferencedNodes();
		} catch (InterruptedException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	public boolean hasNext(String sessionId, String mask) {
		DataPointer ptr = this.nodeMap.get(sessionId);
		if (ptr == null) {
			return false;
		}
		return ptr.getNext() != null;
	}

	public Object poll(String sessionId, String mask) {
		DataPointer ptr = this.nodeMap.get(sessionId);
		if (ptr == null) {
			return null;
		}
		Object obj = ptr.getNode().getValue();
		ptr.moveNext();
		return obj;
	}

	private void fireNewDataArrived() {
		Object[] nodeArray = null;
		LinkedNode lastNode = (LinkedNode) dataQueue.getLast(); // TODO, can be improved, separate set of null pointer
		// nodes
		synchronized (this.nodeMap) {
			nodeArray = this.nodeMap.entrySet().toArray();
		}
		for (int i = 0; i < nodeArray.length; i++) {
			Entry entry = (Entry) nodeArray[i];
			DataPointer ptr = (DataPointer) entry.getValue();
			if (ptr.getNode() == null) {
				ptr.setNode(lastNode);
				ptr.taskItem.schedule(0);
			}
		}
	}

	private void removeNonreferencedNodes() throws InterruptedException {
		LinkedNode node = dataQueue.getHead();
		while (node != null) {
			if (((DataEntry) node.getValue()).isReferenced()) {
				break;
			}
			dataQueue.take();
			node = dataQueue.getHead();
		}
	}

	class DataPointer {
		private LinkedNode node;
		private TaskItem taskItem;

		public DataPointer() {
			this(null);
		}

		public DataPointer(TimerTask timerTask) {
			this.taskItem = new TaskItem(timerTask);
		}

		public void moveNext() {
			if (this.node == null) {
				return;
			}
			this.node = this.node.getNext();
		}

		public boolean hasNext() {
			return node.getNext() != null;
		}

		public LinkedNode getNode() {
			return node;
		}

		public void setNode(LinkedNode node) {
			this.node = node;
		}

		public Object getNext() {
			// TODO
			return null;
		}
	}

	class DataEntry {
		private int referenced;
		private Object obj;

		public DataEntry(Object obj) {
			this.referenced = 0;
			this.obj = obj;
		}

		public boolean isReferenced() {
			return referenced > 0;
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
		DataPointer dataPointer = nodeMap.get(sessionId);
		if (dataPointer != null) {
			((IRequestResponse) dataPointer.taskItem.task).setRequest(request);
			((IRequestResponse) dataPointer.taskItem.task).setResponse(response);
		}

	}

	public void subscribe(String sessionId, TimerTask timerTask) {
		DataPointer dataPointer = new DataPointer(timerTask);
		nodeMap.put(sessionId, dataPointer);
	}

	public void unsubscribe(String sessionId) {
		DataPointer dataPointer = nodeMap.get(sessionId);
		if (dataPointer != null) {
			dataPointer.taskItem.cancel();
			nodeMap.remove(sessionId);
		}
	}
}
