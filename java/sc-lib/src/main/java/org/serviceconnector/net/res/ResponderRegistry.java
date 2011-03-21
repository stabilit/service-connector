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
package org.serviceconnector.net.res;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.registry.Registry;

/**
 * The Class ResponderRegistry. Responder registry stores every responder which completed register process correctly.
 * 
 * @author JTraber
 */
public final class ResponderRegistry extends Registry<Object, IResponder> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ResponderRegistry.class);

	/** The thread local. Space to store any data for a single thread. */
	private ThreadLocal<Object> threadLocal;

	/**
	 * Instantiates a new responder registry.
	 */
	public ResponderRegistry() {
		this.threadLocal = new ThreadLocal<Object>();
	}

	/**
	 * Sets an object in thread local attached to incoming thread. This object can be used later from the same thread.
	 * Other threads cannot access earlier set object.
	 * 
	 * @param obj
	 *            the new thread local
	 */
	public void setThreadLocal(Object obj) {
		this.threadLocal.set(obj);
	}

	/**
	 * Adds an entry of a responder.
	 * 
	 * @param key
	 *            the key
	 * @param responder
	 *            the responder
	 */
	public void addResponder(Object key, IResponder responder) {
		this.put(key, responder);
	}

	/**
	 * Removes the responder.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeResponder(Object key) {
		this.remove(key);
	}

	/**
	 * Gets the responder.
	 * 
	 * @param key
	 *            the key
	 * @return the responder
	 */
	public IResponder getResponder(Object key) {
		return super.get(key);
	}

	/**
	 * Gets all responders.
	 * 
	 * @return the responders
	 */
	public IResponder[] getResponders() {
		try {
			Set<Entry<Object, IResponder>> entries = this.registryMap.entrySet();
			IResponder[] responders = new IResponder[entries.size()];
			int index = 0;
			for (Entry<Object, IResponder> entry : entries) {
				IResponder responder = entry.getValue();
				responders[index++] = responder;
			}
			return responders;
		} catch (Exception e) {
			LOGGER.error("getResponders", e);
		}
		return null;
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

	/**
	 * Gets the first responder matching given connection type.
	 *
	 * @param connectionType the connection type
	 * @return the first responder for connection type
	 */
	public IResponder getFirstResponderForConnectionType(ConnectionType connectionType) {
		IResponder[] responderArray = this.getResponders();
		for (IResponder responder : responderArray) {
 			ListenerConfiguration listenerConfiguration = responder.getListenerConfig();
            if (listenerConfiguration.getConnectionType().equals(connectionType.getValue())) {
            	return responder;
            }
		}
		return null;
	}
}
