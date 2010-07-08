package com.stabilit.scm.common.util;

public class LinkedQueue<E> {

	/**
	 * Dummy header node of list. The first actual node, if it exists, is always at head_.next. After each take, the old
	 * first node becomes the head.
	 **/

	protected LinkedNode<E> head;

	/**
	 * Helper monitor for managing access to last node.
	 **/
	protected final Object putLock = new Object();

	/**
	 * The last node of list. Put() appends to list, so modifies last
	 **/
	protected LinkedNode<E> last;

	protected int size;

	/**
	 * The number of threads waiting for a take. Notifications are provided in put only if greater than zero. The
	 * bookkeeping is worth it here since in reasonably balanced usages, the notifications will hardly ever be
	 * necessary, so the call overhead to notify can be eliminated.
	 **/
	protected int waitingForTake = 0;

	public LinkedQueue() {
		head = new LinkedNode<E>(null);
		last = head;
		this.size = 0;
	}

	public LinkedNode<E> getFirst() {
		return head.next;
	}

	public LinkedNode<E> getLast() {
		return last;
	}

	public int getSize() {
		return size;
	}

	/** Main mechanics for put/offer **/
	protected LinkedNode<E> insert(E x) {
		synchronized (putLock) {
			LinkedNode<E> p = new LinkedNode<E>(x);
			synchronized (last) {
				last.next = p;
				last = p;
				size++;
			}
			if (waitingForTake > 0) {
				putLock.notify();
			}
			return p;
		}
	}

	/** Main mechanics for take/poll **/
	protected synchronized Object extract() {
		synchronized (head) {
			Object x = null;
			LinkedNode<E> first = head.next;
			if (first != null) {
				x = first.value;
				first.value = null;
				head = first;
			}
			size--;
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
			synchronized (putLock) {
				try {
					++waitingForTake;
					for (;;) {
						x = extract();
						if (x != null) {
							--waitingForTake;
							return x;
						} else {
							putLock.wait();
						}
					}
				} catch (InterruptedException ex) {
					--waitingForTake;
					putLock.notify();
					throw ex;
				}
			}
		}
	}

	public Object peek() {
		synchronized (head) {
			LinkedNode<E> first = head.next;
			if (first != null)
				return first.value;
			else
				return null;
		}
	}

	public boolean isEmpty() {
		synchronized (head) {
			return head.next == null;
		}
	}

	public Object poll(long msecs) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		Object x = extract();
		if (x != null)
			return x;
		else {
			synchronized (putLock) {
				try {
					long waitTime = msecs;
					long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
					++waitingForTake;
					for (;;) {
						x = extract();
						if (x != null || waitTime <= 0) {
							--waitingForTake;
							return x;
						} else {
							putLock.wait(waitTime);
							waitTime = msecs - (System.currentTimeMillis() - start);
						}
					}
				} catch (InterruptedException ex) {
					--waitingForTake;
					putLock.notify();
					throw ex;
				}
			}
		}
	}
}
