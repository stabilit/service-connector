/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.srv;

import com.stabilit.scm.common.registry.Registry;

/**
 * @author JTraber
 */
public class SrvServiceRegistry extends Registry {

	private static SrvServiceRegistry instance = new SrvServiceRegistry();

	private SrvServiceRegistry() {
	}

	public static SrvServiceRegistry getCurrentInstance() {
		return instance;
	}

	public void addSrvService(Object key, SrvService srvService) {
		super.put(key, srvService);
	}

	public SrvService getSrvService(String srvServiceName) {
		return (SrvService) this.get(srvServiceName);
	}

	public void removeSrvService(SrvService srvService) {
		this.removeSrvService(srvService.getServiceName());
	}

	public void removeSrvService(Object key) {
		super.remove(key);
	}
}
