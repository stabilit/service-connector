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
package com.stabilit.sc.listener;

import java.util.EventListener;

public class ListenerSupport<T extends EventListener> {

	protected EventListener[] listenerArray;
	protected int size;

	public ListenerSupport() {
		this.listenerArray = new EventListener[16];
		this.size = 0;
	}

	public boolean isEmpty() {
		return size <= 0;
	}
	
	public synchronized void clearAll() {
		this.size = 0;
	}

	public synchronized void addListener(T listener) {
		if (size == this.listenerArray.length) {
			size <<= 1;  // multiply by 2
			EventListener[] newArray = new EventListener[size];
		    System.arraycopy(this.listenerArray, 0, newArray, 0, this.listenerArray.length);
		    this.listenerArray = newArray;
		}
		listenerArray[size++] = listener;
	}
}