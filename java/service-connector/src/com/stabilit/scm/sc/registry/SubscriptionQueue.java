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
import com.stabilit.scm.common.util.ITimerRun;
import com.stabilit.scm.common.util.LinkedQueue;
import com.stabilit.scm.common.util.TimerTaskWrapper;
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
		return ptr.node != null;
	}

	public Object poll(String sessionId, String mask) {
		DataPointer ptr = this.nodeMap.get(sessionId);
		if (ptr == null) {
			return null;
		}
		LinkedNode node = ptr.getNode();
		if (node == null) {
			return null;
		}
		DataEntry dataEntry = (DataEntry) node.getValue();
		dataEntry.dereference();
		ptr.moveNext();
		Object obj = dataEntry.getValue();
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
			}
			if (ptr.isListen()) {
				ptr.schedule(timer, 0);
			}
		}
	}

	private void removeNonreferencedNodes() throws InterruptedException {
		LinkedNode node = dataQueue.getFirst();
		while (node != null) {
			if (((DataEntry) node.getValue()).isReferenced()) {
				break;
			}
			dataQueue.take();
			node = dataQueue.getFirst();
		}
	}

	class DataPointer {
		private LinkedNode node;
		private ITimerRun timerRun;
		private TaskItem taskItem;
		private boolean listen;

		public DataPointer() {
			this(null);
		}

		public DataPointer(ITimerRun timerRun) {
			this.timerRun = timerRun;
			this.taskItem = null;
			this.listen = false;
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

		public void setListen(boolean listen) {
			this.listen = listen;
		}

		public boolean isListen() {
			return listen;
		}

		public void setNode(LinkedNode node) {
			if (node.getValue() == null) {
				return;
			}
			this.node = node;
			DataEntry dataEntry = (DataEntry) this.node.getValue();
			dataEntry.reference();

		}

		public LinkedNode getNext() {
			return this.node;
		}

		public void schedule(Timer timer) {
			this.schedule(timer, this.timerRun.getTimeout());
		}
		public void schedule(Timer timer, int timeout) {
			if (this.taskItem != null) {
				this.taskItem.cancel();
			}
			this.taskItem = new TaskItem(this.timerRun);
			this.taskItem.schedule(this, timeout);
		}
	}

	class DataEntry {
		private int referenced;
		private Object value;

		public DataEntry(Object value) {
			this.referenced = 0;
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public boolean isReferenced() {
			return referenced > 0;
		}

		public void reference() {
			this.referenced++;
		}

		public void dereference() {
			this.referenced--;
		}
	}

	class TaskItem {
		private ITimerRun timerRun;

		public TaskItem(ITimerRun timerRun) {
			this.timerRun = timerRun;
		}

		public void cancel() {
			try {
				TimerTask timerTask = this.timerRun.getTimerTask();
				if (timerTask != null) {
					timerTask.cancel();
				}
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		public void schedule(DataPointer dataPointer, int time) {
			try {
				this.cancel();
				TimerTask timerTask = new SubscriptionTaskWrapper(dataPointer, this.timerRun);
				timer.schedule(timerTask, time * 1000); // TODO constant
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}

	public void listen(String sessionId, IRequest request, IResponse response) {
		DataPointer dataPointer = nodeMap.get(sessionId);
		if (dataPointer != null) {
			((IRequestResponse) dataPointer.timerRun).setRequest(request);
			((IRequestResponse) dataPointer.timerRun).setResponse(response);
		}
		// schedule
		dataPointer.setListen(true);
		dataPointer.schedule(timer);
	}

	public void subscribe(String sessionId, ITimerRun timerRun) {
		DataPointer dataPointer = new DataPointer(timerRun);
		nodeMap.put(sessionId, dataPointer);
	}

	public void unsubscribe(String sessionId) {
		DataPointer dataPointer = nodeMap.get(sessionId);
		if (dataPointer != null) {
			dataPointer.taskItem.cancel();
			nodeMap.remove(sessionId);
		}
	}
	
	class SubscriptionTaskWrapper extends TimerTaskWrapper {

		private DataPointer dataPointer;
		public SubscriptionTaskWrapper(DataPointer dataPointer, ITimerRun target) {
			super(target);
			this.dataPointer = dataPointer;
		}

		@Override
		public void run() {
			dataPointer.setListen(false);
		    super.run();
		}
	}
}
