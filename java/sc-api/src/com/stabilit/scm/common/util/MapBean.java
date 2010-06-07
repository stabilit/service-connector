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
package com.stabilit.scm.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class MapBean, abstracts the java map construct.
 * 
 * @param <T> Type of MapBean
 * @author JTraber
 */
public class MapBean<T> {

	/** The attribute map. */
	protected Map<String, T> attrMap;

	/**
	 * Instantiates a new map bean.
	 */
	public MapBean() {
		attrMap = new HashMap<String, T>();
	}

	/**
	 * Instantiates a new map bean of a given map.
	 * 
	 * @param map
	 *            the map
	 */
	public MapBean(Map<String, T> map) {
		attrMap = map;
	}

	/**
	 * Gets the attribute map.
	 * 
	 * @return the attribute map
	 */
	public Map<String, T> getAttributeMap() {
		return this.attrMap;
	}

	/**
	 * Sets the attribute map.
	 * 
	 * @param attrMap
	 *            the attr map
	 */
	protected void setAttributeMap(Map<String, T> attrMap) {
		this.attrMap = attrMap;
	}

	/**
	 * Gets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @return the attribute
	 */
	public T getAttribute(String name) {
		return this.attrMap.get(name);
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setAttribute(String name, T value) {
		this.attrMap.put(name, value);
	}

	/**
	 * Removes the attribute.
	 * 
	 * @param name
	 *            the name
	 * @return the t
	 */
	public T removeAttribute(String name) {
		return this.attrMap.remove(name);
	}

	/**
	 * Returns key=value; pairs in a string.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		String string = "";
		for (String key : attrMap.keySet()) {
			string += key + "=" + attrMap.get(key) + ";";
		}
		return string;
	}
}
