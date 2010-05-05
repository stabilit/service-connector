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
package com.stabilit.sc.registry.jmx;

import java.beans.ConstructorProperties;

import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public class MapBeanWrapperJMX implements IMapBeanWrapperMXBean {

	MapBean<?> mapBean;
	String registryKey;

	@ConstructorProperties( { "key", "MapBean" })
	public MapBeanWrapperJMX(String key, MapBean<?> mapBean) {
		this.registryKey = key;
		this.mapBean = mapBean;
	}

	@Override
	public String getEntry() {
		return mapBean.toString();
	}

	@Override
	public String getKey() {
		return registryKey;
	}
}
