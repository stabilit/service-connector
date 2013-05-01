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
package org.serviceconnector.registry;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class ServiceRegistry. Registry stores entries for properly configured services.
 * 
 * @author JTraber
 */
public final class ServiceRegistry extends Registry<String, Service> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ServiceRegistry.class);

	/**
	 * Adds the service.
	 * 
	 * @param key
	 *            the key
	 * @param service
	 *            the service
	 */
	public void addService(String key, Service service) {
		super.put(key, service);
	}

	/**
	 * Gets the service.
	 * 
	 * @param key
	 *            the key
	 * @return the service
	 */
	public Service getService(String key) {
		return this.get(key);
	}

	/**
	 * Gets the services.
	 * 
	 * @return the services
	 */
	public Service[] getServices() {
		try {
			Set<Entry<String, Service>> entries = this.registryMap.entrySet();
			Service[] services = new Service[entries.size()];
			int index = 0;
			for (Entry<String, Service> entry : entries) {
				Service service = entry.getValue();
				services[index++] = service;
			}
			return services;
		} catch (Exception e) {
			LOGGER.error("getServices", e);
		}
		return null;
	}

	/**
	 * Removes the service.
	 * 
	 * @param service
	 *            the service
	 */
	public void removeService(Service service) {
		this.removeService(service.getName());
	}

	/**
	 * Removes the service.
	 * 
	 * @param key
	 *            the key
	 * @return the service
	 */
	public Service removeService(String key) {
		return super.remove(key);
	}

	/**
	 * Dump the services into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("services");
		Set<Entry<String, Service>> serviceEntries = this.registryMap.entrySet();
		for (Entry<String, Service> serviceEntry : serviceEntries) {
			Service service = serviceEntry.getValue();
			service.dump(writer);
		}
		writer.writeEndElement(); // end of services
	}
}
