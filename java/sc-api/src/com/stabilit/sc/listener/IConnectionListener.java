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

/**
 * The listener interface for receiving IConnection events. The class that is interested in processing a
 * IConnection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addIConnectionListener<code> method. When
 * the IConnection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ConnectionEvent
 */
public interface IConnectionListener extends EventListener {

	/**
	 * Write event.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 * @throws Exception
	 *             the exception
	 */
	public void writeEvent(ConnectionEvent connectionEvent) throws Exception;

	/**
	 * Read event.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 * @throws Exception
	 *             the exception
	 */
	public void readEvent(ConnectionEvent connectionEvent) throws Exception;

	/**
	 * Connect event.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 * @throws Exception
	 *             the exception
	 */
	public void connectEvent(ConnectionEvent connectionEvent) throws Exception;

	/**
	 * Disconnect event.
	 * 
	 * @param connectionEvent
	 *            the connection event
	 * @throws Exception
	 *             the exception
	 */
	public void disconnectEvent(ConnectionEvent connectionEvent) throws Exception;
}
