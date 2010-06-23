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
 * The listener interface for receiving keep alive events. Each keep alive event points to a connection
 * for type IConnection and tells, that given connection has no activity for given idle timeout
 * 
 * @see KeepAliveEvent
 */
public interface IKeepAliveListener extends EventListener {

	/**
	 * Keep alive event.
	 *
	 * @param keepAliveEvent the keep alive event
	 * @throws Exception the exception
	 */
	public void keepAliveEvent(KeepAliveEvent keepAliveEvent) throws Exception;

}
