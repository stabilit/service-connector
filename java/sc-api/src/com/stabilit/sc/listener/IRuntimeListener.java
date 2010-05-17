/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
 * The listener interface for receiving IWarning events. The class that is interested in processing a IWarning
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addIWarningListener</code> method. When
 * the IWarning event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see RuntimeEvent
 */
public interface IRuntimeListener extends EventListener {

	/**
	 * Warning event.
	 * 
	 * @param warning
	 *            the warning
	 * @throws Exception
	 *             the exception
	 */
	public void runtimeEvent(RuntimeEvent warning) throws Exception;
}
