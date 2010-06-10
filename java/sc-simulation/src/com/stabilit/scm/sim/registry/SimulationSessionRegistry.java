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
package com.stabilit.scm.sim.registry;

import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Session;

/**
 * @author JTraber
 *
 */
public class SimulationSessionRegistry extends SessionRegistry {
	
	private static SimulationSessionRegistry instance = new SimulationSessionRegistry();

	public static SimulationSessionRegistry getCurrentInstance() {
		return instance;
	}	
	
	public void add(Object key, Session session) {
		this.put(key, session);
	}
}
