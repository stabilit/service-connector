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
package com.stabilit.sc.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JTraber
 * 
 */
public class MapBean<T> {

	protected Map<String, T> attrMap;

	public MapBean() {
		attrMap = new HashMap<String, T>();
	}

	public MapBean(Map<String, T> map) {
		attrMap = map;
	}

	public Map<String, T> getAttributeMap() {
		return this.attrMap;
	}

	protected void setAttributeMap(Map<String, T> attrMap) {
		this.attrMap = attrMap;
	}

	public T getAttribute(String name) {
		return this.attrMap.get(name);
	}

	public void setAttribute(String name, T value) {
		this.attrMap.put(name, value);
	}

	public T removeAttribute(String name) {
		return this.attrMap.remove(name);
	}

	@Override
	public String toString() {
		String string = "";
		for (String key : attrMap.keySet()) {
			string += key + "=" + attrMap.get(key) + ";";
		}
		return string;
	}
}
