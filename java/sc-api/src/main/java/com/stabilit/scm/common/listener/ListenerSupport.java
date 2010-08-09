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
		this.listenerArray = null;
		this.size = 0;
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public synchronized void addListener(T listener) {
		if (size >= this.listenerArray.length) {
			EventListener[] newArray = new EventListener[size << 1];   // multiply by 2
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
		for (int i = 0; i < this.size; i++) {
			if (this.listenerArray[i] == listener) {
                if (i < this.size-1) {
                	this.listenerArray[i] = this.listenerArray[this.size-1];
                	this.listenerArray[this.size-1] = null;
                	this.size--;
                	break;
                } else {
                	this.listenerArray[i] = null;                
                	this.size--;
                }
			}
		}
	}
}