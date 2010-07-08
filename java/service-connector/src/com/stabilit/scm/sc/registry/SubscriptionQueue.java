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
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.service.IFilterMask;
import com.stabilit.scm.common.service.IRequestResponse;
import com.stabilit.scm.common.util.ITimerRun;
import com.stabilit.scm.common.util.LinkedNode;
import com.stabilit.scm.common.util.LinkedQueue;
import com.stabilit.scm.common.util.TimerTaskWrapper;

/**
 * The Class SubscriptionQueue.
 * 
 * @param <E>
 *            the element type
 * @author JTraber
 */
public class SubscriptionQueue<E> {

	/** The timer. */
	private Timer timer;

	/** The data queue. */
	private LinkedQueue<E> dataQueue; // the queue

	/** The pointer map. */
	private Map<String, DataPointer> pointerMap; // maps session id to data pointer and its node in queue

	/**
	 * Instantiates a new subscription queue.
	 */
	public SubscriptionQueue() {
		this.dataQueue = new LinkedQueue<E>();
		this.pointerMap = new ConcurrentHashMap<String, DataPointer>();
		this.timer = new Timer("SubscriptionQueue");
	}

	/**
	 * Adds the.
	 * 
	 * @param message
	 *            the message
	 */
	public void add(E message) {
		try {
			dataQueue.put(message);
			fireNewDataArrived();
			removeNonreferencedNodes();
		} catch (InterruptedException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	/**
	 * Checks for next.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the mask
	 * @return true, if successful
	 */
	public boolean hasNext(String sessionId, String mask) {
		DataPointer ptr = this.pointerMap.get(sessionId);
		if (ptr == null) {
			return false;
		}
		return ptr.node != null;
	}

	/**
	 * Poll.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the mask
	 * @return the e
	 */
	public E poll(String sessionId, String mask) {
		DataPointer ptr = this.pointerMap.get(sessionId);
		if (ptr == null) {
			return null;
		}
		LinkedNode<E> node = ptr.getNode();
		if (node == null) {
			return null;
		}
		E message = node.getValue();
		if (message == null) {
			return null;
		}
		node.dereference();
		ptr.moveNext();
		return message;
	}

	/**
	 * Fire new data arrived.
	 */
	private void fireNewDataArrived() {
		Object[] nodeArray = null;
		LinkedNode<E> lastNode = dataQueue.getLast();
		// TODO, can be improved, separate set of null pointer nodes
		synchronized (this.pointerMap) {
			nodeArray = this.pointerMap.values().toArray();
		}
		for (int i = 0; i < nodeArray.length; i++) {
			DataPointer ptr = (DataPointer) nodeArray[i];
			if (ptr.getNode() == null) {
				ptr.setNode(lastNode);
			}
			if (ptr.isListen()) {
				ptr.schedule(timer, 0);
			}
		}
	}

	/**
	 * Removes the non referenced nodes.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private void removeNonreferencedNodes() throws InterruptedException {
		LinkedNode<E> node = dataQueue.getFirst();
		while (node != null) {
			if (node.isReferenced()) {
				break;
			}
			dataQueue.take();
			node = dataQueue.getFirst();
		}
	}

	/**
	 * The Class DataPointer. Points to a queue node. Knows mask for matching messages and state if subscription is
	 * listening or not. Each subscription has his data pointer - its created when client subscribes.
	 */
	private class DataPointer {
		/** The current node in queue. */
		private LinkedNode<E> node;
		/** The timer run. */
		private ITimerRun timerRun;
		/** The filter mask. */
		private IFilterMask filterMask;
		/** The listen state. */
		private boolean listen;
		/** The subscription timeouter. */
		private TimerTask subscriptionTimeouter;

		/**
		 * Instantiates a new DataPointer.
		 * 
		 * @param filterMask
		 *            the filter mask
		 * @param timerRun
		 *            the timer run
		 */
		public DataPointer(IFilterMask filterMask, ITimerRun timerRun) {
			this.timerRun = timerRun;
			this.listen = false;
			this.filterMask = filterMask;
			this.subscriptionTimeouter = null;
		}

		/**
		 * Move next. Moves data pointer to the next node in queue.
		 */
		public void moveNext() {
			if (this.node == null) {
				// current node is already null - no move possible
				return;
			}
			while (true) {
				this.node = this.node.getNext();
				if (this.node == null) {
					// last possible node reached - no next move possible
					break;
				}
				if (this.filterMask.matches(this.node.getValue())) {
					// reached node matches mask keep current position
					return;
				}
			}
		}

		/**
		 * Checks for next.
		 * 
		 * @return true, if successful
		 */
		public boolean hasNext() {
			return node.getNext() != null;
		}

		/**
		 * Gets the current node.
		 * 
		 * @return the node
		 */
		public LinkedNode<E> getNode() {
			return node;
		}

		/**
		 * Sets the listen. If subscription is ready to receive messages listen is true.
		 * 
		 * @param listen
		 *            the new listen
		 */
		public void setListen(boolean listen) {
			this.listen = listen;
		}

		/**
		 * Checks if is listen.
		 * 
		 * @return true, if is listen
		 */
		public boolean isListen() {
			return listen;
		}

		/**
		 * Sets the node.
		 * 
		 * @param node
		 *            the new node
		 */
		public void setNode(LinkedNode<E> node) {
			if (node.getValue() == null) {
				return;
			}
			if (this.filterMask.matches(node.getValue()) == false) {
				// mask doesn't match - don't set the node
				return;
			}
			this.node = node;
			// node is referenced by this data pointer
			this.node.reference();
		}

		/**
		 * Schedule. Activate timeout for no data message.
		 * 
		 * @param timer
		 *            the timer
		 */
		public void schedule(Timer timer) {
			this.schedule(timer, this.timerRun.getTimeout());
		}

		/**
		 * Schedule. Activate timeout with a given time.
		 * 
		 * @param timer
		 *            the timer
		 * @param timeout
		 *            the timeout
		 */
		public void schedule(Timer timer, int timeout) {
			try {
				this.cancel();
				TimerTask timerTask = new SubscriptionTaskWrapper(this, this.timerRun);
				timer.schedule(timerTask, timeout * IConstants.SEC_TO_MILISEC_FACTOR);
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
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
	}

	/**
	 * Listen.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	public void listen(String sessionId, IRequest request, IResponse response) {
		DataPointer dataPointer = pointerMap.get(sessionId);
		if (dataPointer != null) {
			((IRequestResponse) dataPointer.timerRun).setRequest(request);
			((IRequestResponse) dataPointer.timerRun).setResponse(response);
		}
		// schedule
		dataPointer.setListen(true);
		dataPointer.schedule(timer);
	}

	/**
	 * Subscribe.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param filterMask
	 *            the filter mask
	 * @param timerRun
	 *            the timer run
	 */
	public void subscribe(String sessionId, IFilterMask filterMask, ITimerRun timerRun) {
		DataPointer dataPointer = new DataPointer(filterMask, timerRun);
		// Stores sessionId and dataPointer in map
		pointerMap.put(sessionId, dataPointer);
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public void unsubscribe(String sessionId) {
		DataPointer dataPointer = pointerMap.get(sessionId);
		if (dataPointer != null) {
			dataPointer.cancel();
			pointerMap.remove(sessionId);
		}
	}

	/**
	 * The Class SubscriptionTaskWrapper. SubscriptionTaskWrapper times out and calls the target ITimerRun which happens
	 * in super class TimerTaskWrapper. Important to store subscription state in data pointer when time runs out
	 * listening becomes false.
	 */
	private class SubscriptionTaskWrapper extends TimerTaskWrapper {

		/** The data pointer. */
		private DataPointer dataPointer;

		/**
		 * Instantiates a new subscription task wrapper.
		 * 
		 * @param dataPointer
		 *            the data pointer
		 * @param target
		 *            the target
		 */
		public SubscriptionTaskWrapper(DataPointer dataPointer, ITimerRun target) {
			super(target);
			this.dataPointer = dataPointer;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			dataPointer.setListen(false);
			super.run();
		}
	}
}
