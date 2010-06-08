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
package com.stabilit.scm.common.listener;

import java.util.EventListener;

/**
 * The Class ListenerSupport. Manages all the listeners.
 * 
 * @param <T>
 *            type of ListenerSupport
 * @author JTraber
 */
public class ListenerSupport<T extends EventListener> {

	/** The listener array. */
	protected EventListener[] listenerArray;
	/** The size. */
	protected int size;

	/**
	 * Instantiates a new listener support.
	 */
	public ListenerSupport() {
		this.listenerArray = new EventListener[16];
		this.size = 0;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return size <= 0;
	}

	/**
	 * Clear all.
	 */
	public synchronized void clearAll() {
		this.size = 0;
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public synchronized void addListener(T listener) {
		if (size == this.listenerArray.length) {
			size <<= 1; // multiply by 2
			EventListener[] newArray = new EventListener[size];
			System.arraycopy(this.listenerArray, 0, newArray, 0, this.listenerArray.length);
			this.listenerArray = newArray;
		}
		listenerArray[size++] = listener;
	}
	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public synchronized void removeListener(T listener) {
		EventListener[] newArray = new EventListener[size];
		int newIndex = 0;
		for (int i = 0; i < this.listenerArray.length; i++) {
			if (this.listenerArray[i] != listener) {
				newArray[newIndex++] = this.listenerArray[i];
			}
		}
		this.size = newIndex;
		this.listenerArray = newArray;
	}
}