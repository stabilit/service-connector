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
package com.stabilit.sc.common.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public class ListenerSupport<T extends EventListener> {

	protected List<T> listenerList;
	protected List<T> unmodifiableList;

	public ListenerSupport() {
		this.listenerList = new ArrayList<T>();
		this.unmodifiableList = this.listenerList;
	}

	public boolean isEmpty() {
		return this.listenerList.isEmpty();
	}
	
	public synchronized void clearAll() {
		this.listenerList.clear();
	}

	public synchronized void addListener(T listener) {
		listenerList.add(listener);
		this.unmodifiableList = listenerList;
	}

	public synchronized void removeListener(T listener) {
		listenerList.remove(listener);
		this.unmodifiableList = listenerList;
	}

}
