/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
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
