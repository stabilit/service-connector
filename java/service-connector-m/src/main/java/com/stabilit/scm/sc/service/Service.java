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

/**
 * @author JTraber
 */
public abstract class Service {
	private ServiceType type;
	private String name;
	private String location;
	protected int serverIndex;
	protected List<Server> listOfServers;

	public Service(String name, ServiceType type) {
		this.name = name;
		this.location = null;
		this.serverIndex = 0;
		this.type = type;
		// synchronize the sever list
		this.listOfServers = Collections.synchronizedList(new ArrayList<Server>());
	}

	public String getServiceName() {
		return name;
	}

	public void addServer(Server server) {
		this.listOfServers.add(server);
	}

	public void removeServer(Server server) {
		this.listOfServers.remove(server);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ServiceType getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.serverIndex);
		for (Server server : this.listOfServers) {
			sb.append(" - ");
			sb.append(server);
		}
		return sb.toString();
	}
}
