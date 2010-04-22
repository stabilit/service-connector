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
package com.stabilit.sc.common.registry;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.io.Session;

/**
 * @author JTraber
 * 
 */
public final class SessionRegistry extends Registry {

	private static SessionRegistry instance = new SessionRegistry();

	private SessionRegistry() {
		log = Logger.getLogger("sessionRegistry." + SessionRegistry.class.getName());
	}

	public static SessionRegistry getCurrentInstance() {
		return instance;
	}

	public void add(Object key, Session session) {
		this.put(key, session);
	}
}
