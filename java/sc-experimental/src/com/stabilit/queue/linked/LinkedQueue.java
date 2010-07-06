package com.stabilit.queue.linked;

public class LinkedQueue<E> {

	/**
	 * Dummy header node of list. The first actual node, if it exists, is always
	 * at head_.next. After each take, the old first node becomes the head.
	 **/
	protected LinkedNode<E> head_;

	/**
	 * Helper monitor for managing access to last node.
	 **/
	protected final Object putLock_ = new Object();

	/**
	 * The last node of list. Put() appends to list, so modifies last_
	 **/
	protected LinkedNode<E> last_;

	/**
	 * The number of threads waiting for a take. Notifications are provided in
	 * put only if greater than zero. The bookkeeping is worth it here since in
	 * reasonably balanced usages, the notifications will hardly ever be
	 * necessary, so the call overhead to notify can be eliminated.
	 **/
	protected int waitingForTake_ = 0;

	public LinkedQueue() {
		head_ = new LinkedNode<E>(null);
		last_ = head_;
	}

	/** Main mechanics for put/offer **/
	protected INode<E> insert(E x) {
		synchronized (putLock_) {
			LinkedNode<E> p = new LinkedNode<E>(x);
			synchronized (last_) {
				last_.next = p;
				last_ = p;
			}
			if (waitingForTake_ > 0) {
				putLock_.notify();
			}
			return p;				
		}
	}

	/** Main mechanics for take/poll **/
	protected synchronized Object extract() {
		synchronized (head_) {
			Object x = null;
			LinkedNode<E> first = head_.next;
			if (first != null) {
				x = first.value;
				first.value = null;
				head_ = first;
			}
			return x;
		}
	}

	public void put(E x) throws InterruptedException {
		if (x == null)
			throw new IllegalArgumentException();
		if (Thread.interrupted())
			throw new InterruptedException();
		insert(x);
	}

	public boolean offer(E x, long msecs) throws InterruptedException {
		if (x == null)
			throw new IllegalArgumentException();
		if (Thread.interrupted())
			throw new InterruptedException();
		insert(x);
		return true;
	}

	public Object take() throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		// try to extract. If fail, then enter wait-based retry loop
		Object x = extract();
		if (x != null)
			return x;
		else {
			synchronized (putLock_) {
				try {
					++waitingForTake_;
					for (;;) {
						x = extract();
						if (x != null) {
							--waitingForTake_;
							return x;
						} else {
							putLock_.wait();
						}
					}
				} catch (InterruptedException ex) {
					--waitingForTake_;
					putLock_.notify();
					throw ex;
				}
			}
		}
	}

	public Object peek() {
		synchronized (head_) {
			LinkedNode<E> first = head_.next;
			if (first != null)
				return first.value;
			else
				return null;
		}
	}

	public boolean isEmpty() {
		synchronized (head_) {
			return head_.next == null;
		}
	}

	public Object poll(long msecs) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		Object x = extract();
		if (x != null)
			return x;
		else {
			synchronized (putLock_) {
				try {
					long waitTime = msecs;
					long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
					++waitingForTake_;
					for (;;) {
						x = extract();
						if (x != null || waitTime <= 0) {
							--waitingForTake_;
							return x;
						} else {
							putLock_.wait(waitTime);
							waitTime = msecs
									- (System.currentTimeMillis() - start);
						}
					}
				} catch (InterruptedException ex) {
					--waitingForTake_;
					putLock_.notify();
					throw ex;
				}
			}
		}
	}
	
	interface INode<E> {
        public abstract INode<E> getNext();
        public abstract E getValue();
	}
	
	class LinkedNode<E> implements INode<E>{
		public E value;
		public LinkedNode next;

		public LinkedNode() {
		}

		public LinkedNode(E x) {
			value = x;
		}

		public LinkedNode(E x, LinkedNode<E> n) {
			value = x;
			next = n;
		}
		
		@Override
		public INode<E> getNext() {
			return next;
		}
		
		public E getValue() {
			return value;
		}
	}
}

