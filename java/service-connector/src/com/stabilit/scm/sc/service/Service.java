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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.common.util.MapBean;

/**
 * @author JTraber
 */
public class Service extends MapBean<String> {

	private String type; // todo enum machen oder klasse
	private String name;
	private String location;
	private int serverIndex;
	private List<Server> listOfServers;

	public Service(String name) {
		this.name = name;
		this.type = null;
		this.location = null;
		this.serverIndex = 0;
		this.listOfServers = Collections.synchronizedList(new ArrayList<Server>());
	}

	public String getServiceName() {
		return name;
	}

	public void addServer(Server server) {
		listOfServers.add(server);
	}

	public void removeServer(Server server) {
		listOfServers.remove(server);
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

	public synchronized Server allocateServer() {
		for (int i = 0; i < listOfServers.size(); i++) {
			// increment serverIndex
			serverIndex++;
			if (serverIndex >= listOfServers.size()) {
				// serverIndex reached the end of list no more servers
				serverIndex = 0;
			}
			Server server = listOfServers.get(serverIndex);
			if (server.hasFreeSession()) {
				return server;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(serverIndex);		
		for (Server server : listOfServers) {
			sb.append(" - ");
			sb.append(server);			
		}
		return sb.toString();
	}
}
