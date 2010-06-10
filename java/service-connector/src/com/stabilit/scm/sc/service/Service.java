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
package com.stabilit.scm.sc.service;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.MapBean;

/**
 * @author JTraber
 *
 */
public class Service extends MapBean<String> {
	
	private String type; //todo enum machen oder klasse
	private String name;
	private String location;
	
	private Map<String, Server> listOfServers;

	public Service(String name) {
		this.name = name;
		this.type = null;
		this.location = null;
		this.listOfServers = new HashMap<String, Server>();
	}
	
	public String getServiceName() {
		return name;
	}

	public void addServer(Server server) {
		listOfServers.put(server.getSocketAddress().toString(), server);
	}
	
	public void removeServer(Server server) {
		listOfServers.remove(server.getSocketAddress().toString());
	}	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Server allocateServer(SCMPMessage scmp) {
		
		
		
		return null;
	}
}
