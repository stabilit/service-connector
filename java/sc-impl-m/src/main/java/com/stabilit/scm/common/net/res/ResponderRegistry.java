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
package com.stabilit.scm.common.net.res;

import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.common.res.IResponder;

/**
 * The Class ResponderRegistry. Responder registry stores every responder which completed register process correctly.
 * 
 * @author JTraber
 */
public final class ResponderRegistry extends Registry<Object, IResponder> {

	/** The instance. */
	private static ResponderRegistry instance = new ResponderRegistry();
	/** The thread local. Space to store any data for a single thread. */
	private ThreadLocal<Object> threadLocal;

	/**
	 * Instantiates a new responder registry.
	 */
	private ResponderRegistry() {
		threadLocal = new ThreadLocal<Object>();
	}

	/**
	 * Sets an object in thread local attached to incoming thread. This object can be used later from the same thread.
	 * Other threads can not access earlier set object.
	 * 
	 * @param obj
	 *            the new thread local
	 */
	public void setThreadLocal(Object obj) {
		this.threadLocal.set(obj);
	}

	/**
	 * Gets the current instance of responder registry.
	 * 
	 * @return the current instance
	 */
	public static ResponderRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds an entry of a responder.
	 * 
	 * @param key
	 *            the key
	 * @param item
	 *            the item
	 */
	public void addResponder(Object key, IResponder responder) {
		this.put(key, responder);
	}

	public IResponder getResponder(Object key) {
		return super.get(key);
	}

	/**
	 * Gets the current responder.
	 * 
	 * @return the current responder
	 */
	public IResponder getCurrentResponder() {
		// gets the key from thread local, key has been set before of the same thread by calling method
		// setThreadLocal(object obj), very useful to identify current responder
		Object key = this.threadLocal.get();
		IResponder responder = this.getResponder(key);
		return responder;
	}
}
