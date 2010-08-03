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
 * The listener interface for receiving ISCMP events. The class that is interested in processing a ISCMP event
 * implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addISCMPListener<code> method. When
 * the ISCMP event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ISCMPEvent
 */
public interface ISCMPListener extends EventListener {

	/**
	 * Encode event.
	 * 
	 * @param scmpEvent
	 *            the scmp event
	 */
	public abstract void encodeEvent(SCMPEvent scmpEvent);

	/**
	 * Decode event.
	 * 
	 * @param scmpEvent
	 *            the scmp event
	 */
	public abstract void decodeEvent(SCMPEvent scmpEvent);
}
