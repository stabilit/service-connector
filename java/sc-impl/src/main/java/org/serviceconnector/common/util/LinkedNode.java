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
package org.serviceconnector.common.util;

import org.apache.log4j.Logger;

/**
 * The Class LinkedNode. Represents a node of the LinkedQueue construct.
 * 
 * @param <T>
 *            the generic type
 */
public class LinkedNode<T> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(LinkedNode.class);
	
	/** The value of the node. */
	public T value;
	/** The next node in queue. */
	public LinkedNode<T> next;
	/** The referenced - counts nodes referencing components. */
	private int referenced;

	/**
	 * Instantiates a LinkedNode.
	 * 
	 * @param value
	 *            the value
	 */
	LinkedNode(T value) {
		this.value = value;
	}

	/**
	 * Instantiates a LinkedNode.
	 * 
	 * @param value
	 *            the value
	 * @param next
	 *            the next
	 */
	public LinkedNode(T value, LinkedNode<T> next) {
		this.value = value;
		this.next = next;
	}

	/**
	 * Gets the next node.
	 * 
	 * @return the next node
	 */
	public LinkedNode<T> getNext() {
		return next;
	}

	/**
	 * Gets the value of the node.
	 * 
	 * @return the value of the node
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Checks if is referenced.
	 * 
	 * @return true, if is referenced
	 */
	public boolean isReferenced() {
		return referenced > 0;
	}

	/**
	 * Increment reference counter.
	 */
	public void reference() {
		this.referenced++;
	}

	/**
	 * Decrement reference counter.
	 */
	public void dereference() {
		this.referenced--;
	}
}
