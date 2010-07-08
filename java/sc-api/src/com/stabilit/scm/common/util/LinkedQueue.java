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
package com.stabilit.scm.common.util;

/**
 * The Class LinkedQueue. Base Queue to implement consumer/producer modules.
 * 
 * @param <E>
 *            the element type
 */
public class LinkedQueue<E> {
	/**
	 * The first actual node, if it exists, is always at this.head.next. After each take, the old first node becomes the
	 * this.head.
	 */
	private LinkedNode<E> head;
	/** The this.last node of list. Put() appends to list, so modifies this.last */
	private LinkedNode<E> last;
	/** The current this.size of the queue. */
	private int size;

	/**
	 * Instantiates a LinkedQueue.
	 */
	public LinkedQueue() {
		// this.head element is a LinkedNode with null value
		this.head = new LinkedNode<E>(null);
		this.last = this.head;
		this.size = 0;
	}

	/**
	 * Gets the first node.
	 * 
	 * @return the first
	 */
	public LinkedNode<E> getFirst() {
		return this.head.next;
	}

	/**
	 * Gets the this.last.
	 * 
	 * @return the this.last
	 */
	public LinkedNode<E> getLast() {
		return this.last;
	}

	/**
	 * Gets the this.size.
	 * 
	 * @return the this.size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Main mechanics to insert in queue.
	 * 
	 * @param value
	 *            the value for new node
	 * @return the linked node
	 */
	public LinkedNode<E> insert(E value) {
		LinkedNode<E> newNode = new LinkedNode<E>(value);
		synchronized (this.last) {
			this.last.next = newNode;
			this.last = newNode;
			this.size++;
		}
		return newNode;
	}

	/**
	 * Main mechanics for extract from queue. If no message in queue null will be returned.
	 * 
	 * @return the object
	 */
	public synchronized E extract() {
		synchronized (this.head) {
			E value = null;
			LinkedNode<E> first = this.head.next;
			if (first != null) {
				value = first.value;
				first.value = null;
				this.head = first;
				this.size--;
			}
			return value;
		}
	}

	/**
	 * Peek. Returns first value - if there is no first node null will be returned.
	 * 
	 * @return the object
	 */
	public E peek() {
		synchronized (this.head) {
			LinkedNode<E> first = this.head.next;
			if (first != null) {
				return first.value;
			} else {
				return null;
			}
		}
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		synchronized (this.head) {
			return this.head.next == null;
		}
	}
}
