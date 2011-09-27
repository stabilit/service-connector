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
package org.serviceconnector.registry;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.ReceivePublicationTimeout;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.LinkedNode;
import org.serviceconnector.util.LinkedQueue;
import org.serviceconnector.util.NamedPriorityThreadFactory;

/**
 * The Class PublishMessageQueue. The PublishMessageQueue is responsible for queuing incoming data from server, to inform
 * subscriptions about new arrived messages, to observe there timeouts and to know there current position in queue
 * (TimeAwareDataPointer). The queue needs also to handle the deleting of consumed messages and to assure queue does not overflow.
 * 
 * @param <E>
 *            the element type to handle in the queue
 * @author JTraber
 */
public class PublishMessageQueue<E> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(PublishMessageQueue.class);

	/** The timeout scheduler. */
	private ScheduledThreadPoolExecutor timeoutScheduler;
	/** The data queue. */
	private LinkedQueue<E> dataQueue;
	/** The pointer map - maps session id to data pointer and its node in queue. */
	private Map<String, TimeAwareDataPointer> pointerMap;
	/** The peak the queue ever reached. */
	private int peak;

	/**
	 * Instantiates a new PublishMessageQueue.
	 */
	public PublishMessageQueue() {
		this.dataQueue = new LinkedQueue<E>();
		this.pointerMap = new ConcurrentHashMap<String, TimeAwareDataPointer>();
		this.timeoutScheduler = new ScheduledThreadPoolExecutor(5, new NamedPriorityThreadFactory("CRPTimeout"));
	}

	/**
	 * Iterator<LinkedNode<E>>.
	 * 
	 * @return the iterator
	 */
	public Iterator<LinkedNode<E>> nodeIterator() {
		return this.dataQueue.nodeIterator();
	}

	/**
	 * Gets the total size of the queue.
	 * 
	 * @return the size
	 */
	public int getTotalSize() {
		return this.dataQueue.getSize();
	}

	/**
	 * Gets the referenced node count.
	 * 
	 * @return the referenced node count
	 */
	public int getReferencedNodesCount() {
		Iterator<LinkedNode<E>> iter = this.dataQueue.nodeIterator();
		int nonRefElemCount = 0;
		while (iter.hasNext()) {
			// this loop stops when first referenced node in queue is found
			LinkedNode<E> linkedNode = (LinkedNode<E>) iter.next();
			if (linkedNode.isReferenced() == false) {
				nonRefElemCount++;
			} else {
				break;
			}
		}
		// return number of referenced nodes in queue
		return this.dataQueue.getSize() - nonRefElemCount;
	}

	/**
	 * Inserts a new message into the queue.
	 * 
	 * @param message
	 *            the message
	 */
	public synchronized void insert(E message) {
		if (message == null) {
			// inserting null value not allowed
			return;
		}
		this.dataQueue.insert(message);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("insert - queue size=" + this.dataQueue.getSize());
		}
		int currSize = this.dataQueue.getSize();
		if (currSize > this.peak) {
			this.peak = currSize;
		}
		// inform new message arrived
		this.fireNewDataArrived();
		// delete unreferenced nodes in queue
		this.removeNonreferencedNodes();
	}

	/**
	 * Checks for next.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return true, if successful
	 */
	public boolean hasNext(String sessionId) {
		TimeAwareDataPointer ptr = this.pointerMap.get(sessionId);
		return ptr.node != null;
	}

	/**
	 * Return message if any. If no message is available null will be returned.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the e
	 */
	public synchronized E getMessage(String sessionId) {
		TimeAwareDataPointer ptr = this.pointerMap.get(sessionId);
		LinkedNode<E> node = ptr.getNode();
		if (node == null) {
			// nothing to poll data pointer points to null - return null
			return null;
		}
		E message = node.getValue();
		if (message == null) {
			return null;
		}
		// dereference node, pointer moves to next node
		node.dereference();
		ptr.moveNext();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getMessage - queue size=" + this.dataQueue.getSize());
		}
		return message;

	}

	/**
	 * Return message if any. If no message is available null will be returned.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the e
	 */
	public synchronized E getMessageOrListen(String sessionId, IRequest request, IResponse response) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getMessageOrListen");
		}
		E message = this.getMessage(sessionId);
		if (message == null) {
			// message null - switch to listen mode
			this.listen(sessionId, request, response);
			return null;
		}
		return message;
	}

	/**
	 * Fire new data arrived. Indicates that a new message has been added. Sets data pointer pointing on null elements to new
	 * element
	 * if necessary (mask matches & listening mode).
	 */
	private synchronized void fireNewDataArrived() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("fireNewDataArrived");
		}
		LinkedNode<E> newNode = dataQueue.getLast();
		for (TimeAwareDataPointer ptr : this.pointerMap.values()) {
			if (ptr.getNode() == null) {
				// data pointer points to null - try pointing to new element
				if (ptr.setNode(newNode) == true) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("data pointer points to null,setNode successful - data pointer interested in new node");
					}
					// setNode successful - data pointer interested in new node
					if (ptr.listening()) {
						// data pointer in listen mode needs to be informed about new data
						ptr.schedule(0);
					}
				}
			}
		}
	}

	/**
	 * Removes the non referenced nodes. Starts removing nodes in first position of queue - stops at the position a node is
	 * referenced.
	 */
	public synchronized void removeNonreferencedNodes() {
		LinkedNode<E> node = this.dataQueue.getFirst();
		while (node != null) {
			if (node.isReferenced()) {
				// stop removing nodes at the position you get a referenced node
				break;
			}
			// remove node
			this.dataQueue.extract();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("remove - queue size=" + this.dataQueue.getSize());
			}
			// reads next node
			node = this.dataQueue.getFirst();
		}
	}

	/**
	 * Listen. Indicates that client is ready for messages. Data pointer changes to listen mode and schedules timeout.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	private synchronized void listen(String sessionId, IRequest request, IResponse response) {
		TimeAwareDataPointer dataPointer = this.pointerMap.get(sessionId);
		// stores request/response in timer run - to answer client correctly at timeout
		dataPointer.crpTimeout.setRequest(request);
		dataPointer.crpTimeout.setResponse(response);
		// starts listening and schedules subscription timeout
		dataPointer.startListen();
		dataPointer.schedule();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("PublishMessageQueue listen sid=" + sessionId + " listen=" + dataPointer.listening);
		}
	}

	/**
	 * Subscribe. Sets up subscription, create data pointer.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the filter mask
	 * @param crpTimeout
	 *            the timer run
	 */
	public synchronized void subscribe(String sessionId, SubscriptionMask mask, ReceivePublicationTimeout crpTimeout) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("subscribe");
		}
		TimeAwareDataPointer dataPointer = new TimeAwareDataPointer(mask, crpTimeout);
		// Stores sessionId and dataPointer in map
		this.pointerMap.put(sessionId, dataPointer);
	}

	/**
	 * Change subscription.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the mask
	 */
	public synchronized void changeSubscription(String sessionId, SubscriptionMask mask) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("changeSubscription");
		}
		TimeAwareDataPointer dataPointer = this.pointerMap.get(sessionId);
		if (dataPointer != null) {
			dataPointer.changeMask(mask);
		}
	}

	/**
	 * Unsubscribe. Deletes subscription, remove data pointer.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public synchronized void unsubscribe(String sessionId) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("unsubscribe");
		}
		TimeAwareDataPointer dataPointer = this.pointerMap.get(sessionId);
		if (dataPointer != null && dataPointer.listening) {
			// unsubscribe & pointer is in listen mode - run a timeout
			dataPointer.cancel();
			dataPointer.crpTimeout.timeout();
			dataPointer.stopListen();
		}
		this.pointerMap.remove(sessionId);
		if (dataPointer != null) {
			dataPointer.destroy();
		}
	}

	/**
	 * Gets the peak the queue ever reached.
	 * 
	 * @return the peak
	 */
	public int getPeak() {
		return this.peak;
	}

	/**
	 * The Class TimeAwareDataPointer. Points to a queue node. Knows mask for matching messages and state if subscription is
	 * listening or not. Each subscription has his data pointer - its created when client subscribes.
	 */
	private class TimeAwareDataPointer {
		/** The current node in queue. */
		private LinkedNode<E> node;
		/** The publishTimeout. */
		private ReceivePublicationTimeout crpTimeout;
		/** The subscription mask. */
		private SubscriptionMask mask;
		/** The listen state. */
		private boolean listening;
		/** The timeout. */
		private ScheduledFuture<PublishTimeoutWrapper> timeout;

		/**
		 * Instantiates a new TimeAwareDataPointer.
		 * 
		 * @param mask
		 *            the filter mask
		 * @param crpTimeout
		 *            the publish timeout
		 */
		public TimeAwareDataPointer(SubscriptionMask mask, ReceivePublicationTimeout crpTimeout) {
			this.crpTimeout = crpTimeout;
			this.listening = false;
			this.mask = mask;
		}

		/**
		 * Move next. Moves data pointer to the next node in queue.
		 */
		private void moveNext() {
			if (this.node == null) {
				// current node is already null - no move possible
				return;
			}
			while (true) {
				this.node = this.node.getNext();
				if (this.node == null) {
					// last possible node reached - no next move possible
					return;
				}
				if (this.mask.matches((SCMPMessage) this.node.getValue())) {
					this.node.reference();
					// reached node matches mask keep current position
					return;
				}
			}
		}

		/**
		 * Change mask.
		 * 
		 * @param mask
		 *            the mask
		 */
		private void changeMask(SubscriptionMask mask) {
			this.mask = mask;
			if (this.node == null) {
				return;
			}
			if (this.mask.matches((SCMPMessage) this.node.getValue())) {
				// current node matches new mask keep current position
				return;
			} else {
				// TODO JOT this.node.dereference();
				// move to next matching node
				this.moveNext();
			}
		}

		/**
		 * Checks for next.
		 * 
		 * @return true, if successful
		 */
		@SuppressWarnings("unused")
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
		 * Start listen. If subscription is ready to receive messages listen is true.
		 */
		public void startListen() {
			this.listening = true;
		}

		/**
		 * Stop listen. If subscription is not ready to receive messages listen is false.
		 */
		public void stopListen() {
			this.listening = false;
		}

		/**
		 * Checks if is listen.
		 * 
		 * @return true, if is listen
		 */
		public boolean listening() {
			return this.listening;
		}

		/**
		 * Sets the node.
		 * 
		 * @param node
		 *            the new node
		 * @return true, if successful
		 */
		public boolean setNode(LinkedNode<E> node) {
			if (this.mask.matches((SCMPMessage) node.getValue()) == false) {
				// mask doesn't match - don't set the node
				return false;
			}
			// set the node
			this.node = node;
			// node needs to be referenced by this data pointer
			this.node.reference();
			return true;
		}

		/**
		 * Schedule. Activate timeout for no data message.
		 */
		public synchronized void schedule() {
			this.schedule(this.crpTimeout.getTimeoutMillis());
		}

		/**
		 * Schedule. Activate subscription timeout with a given time.
		 * 
		 * @param timeoutMillis
		 *            the timeout
		 */
		@SuppressWarnings("unchecked")
		public synchronized void schedule(double timeoutMillis) {
			// always cancel old timeouter when schedule of an new timeout is necessary
			this.cancel();
			PublishTimeoutWrapper timeoutWrapper = new PublishTimeoutWrapper(this, this.crpTimeout);
			// schedules timeoutTask on subscription queue executer
			this.timeout = (ScheduledFuture<PublishTimeoutWrapper>) timeoutScheduler.schedule(timeoutWrapper, (long) timeoutMillis,
					TimeUnit.MILLISECONDS);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("schedule datapointer " + timeoutMillis);
			}
		}

		/**
		 * Destroys data pointer and dereferences node in queue.
		 */
		private synchronized void destroy() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("destroy TimeAwareDataPointer");
			}
			this.cancel();
			if (node != null) {
				this.node.dereference();
			}
			if (this.listening) {
				// necessary for responding CRP to client! Very important!
				this.listening = false;
				this.crpTimeout.timeout();
			}
			this.node = null;
			this.crpTimeout = null;
			this.mask = null;
		}

		/**
		 * Cancel. Deactivate subscription timeout.
		 */
		public synchronized void cancel() {
			if (this.timeout != null) {
				// cancel timeout even if its already running - timeout synchronize on queue
				this.timeout.cancel(true);
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("cancel TimeAwareDataPointer");
				}
				// important to set timeouter null - rescheduling of same instance not possible
				this.timeout = null;
				// removes canceled timeouts
				PublishMessageQueue.this.timeoutScheduler.purge();
			}
		}
	}

	/**
	 * The Class PublishTimeoutWrapper. PublishTimeoutWrapper times out and calls the target ITimerRun. Important to store
	 * subscription state in data pointer when time runs out listening becomes false.
	 */
	private class PublishTimeoutWrapper implements Runnable {

		/** The data pointer. */
		private TimeAwareDataPointer dataPointer;
		/** The target. */
		private ITimeout target;

		/**
		 * Instantiates a PublishTimeoutWrapper.
		 * 
		 * @param dataPointer
		 *            the data pointer
		 * @param target
		 *            the target
		 */
		public PublishTimeoutWrapper(TimeAwareDataPointer dataPointer, ITimeout target) {
			this.dataPointer = dataPointer;
			this.target = target;
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			synchronized (PublishMessageQueue.this) {
				// stops listening - ITimerRun gets executed
				this.dataPointer.stopListen();
				// timeout target
				this.target.timeout();
			}
		}
	}
}