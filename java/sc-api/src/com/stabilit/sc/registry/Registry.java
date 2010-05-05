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
package com.stabilit.sc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.stabilit.sc.registry.jmx.IRegistryMXBean;
import com.stabilit.sc.registry.jmx.MapBeanWrapperJMX;
import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public abstract class Registry implements IRegistry, IRegistryMXBean {

	private Map<Object, MapBean<?>> registryMap;
	protected Logger log;
	
	public Registry() {
		registryMap = new ConcurrentHashMap<Object, MapBean<?>>();
	}

	protected void put(Object key, MapBean<?> value) {
		registryMap.put(key, value);
		log.debug("Entry added: " + key + "=" + value);
	}

	public MapBean<?> get(Object key) {
		return registryMap.get(key);
	}
	
	public void remove(Object key) {
		log.debug("Entry removed: " + key + "=" + registryMap.get(key));
		this.registryMap.remove(key);
	}
	
	public boolean containsKey(Object key) {
		return registryMap.containsKey(key);
	}

	@Override
	public String toString() {
		StringBuffer dump = new StringBuffer();
		for (Object key : registryMap.keySet()) {
			dump.append(key);
			dump.append(":");
			dump.append(registryMap.get(key).toString());
		}
		return dump.toString();
	}
	
	@Override
	public MapBeanWrapperJMX[] getEntries() {
		MapBeanWrapperJMX[] mapBeanStringJMX = new MapBeanWrapperJMX[registryMap.size()];
		int i = 0;
		for (Object key : registryMap.keySet()) {
			mapBeanStringJMX[i] = new MapBeanWrapperJMX(key.toString(),(MapBean<?>) registryMap.get(key) );
			i++;
		}
		return mapBeanStringJMX;
	}
}
