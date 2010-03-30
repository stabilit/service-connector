package com.stabilit.sc.cln.io;


import java.util.ArrayList;
import java.util.List;

import com.stabilit.sc.common.registry.Registry;
import com.stabilit.sc.common.util.MapBean;

/**
 * @author JTraber
 *
 */
public class SCMPSessionRegistry extends Registry {

	private static SCMPSessionRegistry sessionRegistry = new SCMPSessionRegistry();
	
	public static SCMPSessionRegistry getCurrentInstance() {
		return sessionRegistry;
	}
	
	private SCMPSessionRegistry() {
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
