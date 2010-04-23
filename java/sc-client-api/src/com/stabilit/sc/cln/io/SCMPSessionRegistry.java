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
package com.stabilit.sc.cln.io;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.registry.Registry;
import com.stabilit.sc.common.util.MapBean;

/**
 * @author JTraber
 *
 */
public final class SCMPSessionRegistry extends Registry {

	private static SCMPSessionRegistry sessionRegistry = new SCMPSessionRegistry();
	
	public static SCMPSessionRegistry getCurrentInstance() {
		return sessionRegistry;
	}
	
	private SCMPSessionRegistry() {
		log = Logger.getLogger(SCMPSessionRegistry.class);
		MapBean<?> sessionIdMapBean = new MapBean<List<String>>();
		this.put("sessionIdMapBean", sessionIdMapBean);
		MapBean<?> serviceNameMapBean = new MapBean<List<String>>();
		this.put("serviceNameMapBean", serviceNameMapBean);

	}

	@SuppressWarnings("unchecked")
	public synchronized void add(String sessionId, String serviceName) {
		MapBean<List<String>> sessionIdMapBean = (MapBean<List<String>>) this.get("sessionIdMapBean");
        List<String> sessionIdList = sessionIdMapBean.getAttribute(sessionId);
        if (sessionIdList == null) {
        	sessionIdList = new ArrayList<String>();
        	sessionIdMapBean.setAttribute(sessionId, sessionIdList);
        }
        sessionIdList.add(serviceName);
        
		MapBean<List<String>> serviceNameMapBean = (MapBean<List<String>>) this.get("serviceNameMapBean");
        List<String> serviceNameList = sessionIdMapBean.getAttribute(serviceName);
        if (serviceNameList == null) {
        	serviceNameList = new ArrayList<String>();
        	serviceNameMapBean.setAttribute(serviceName, serviceNameList);
        }
        serviceNameList.add(sessionId);
	}

	@SuppressWarnings("unchecked")
	public synchronized void remove(String sessionId, String serviceName) {
		MapBean<List<String>> sessionIdMapBean = (MapBean<List<String>>) this.get("sessionIdMapBean");
        List<String> sessionIdList = sessionIdMapBean.getAttribute(sessionId);
        if (sessionIdList != null) {
        	sessionIdList.remove(serviceName);
        }
        
		MapBean<List<String>> serviceNameMapBean = (MapBean<List<String>>) this.get("serviceNameMapBean");
        List<String> serviceNameList = sessionIdMapBean.getAttribute(serviceName);
        if (serviceNameList != null) {
        	serviceNameList.remove(sessionId);
        }
	}
	
	
	
	
}
