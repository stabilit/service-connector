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
package com.example.jmx.mxbean;

import java.beans.ConstructorProperties;
import java.util.Map;

/**
 * @author JTraber
 *
 */
public class MapBeanSample implements MapBeanSampleMXBean{

	MapBean<String> mapBean;
	
	@ConstructorProperties({"MapBean"})
	public MapBeanSample(MapBean que) {
		this.mapBean = que;
	}
	
	public String[] getAttributes() {
		Map<String, String> attributeMap = mapBean.getAttributeMap();
		String[] attributes = new String[attributeMap.size()];
		
		int index = 0;
		for (String value : attributeMap.values()) {
			attributes[index] = value;
			index++;
		}
		return attributes;
	}
	
	@Override
	public RegistryEntry[] getTest() {
		Map<String, String> attributeMap = mapBean.getAttributeMap();
		RegistryEntry[] tests = new RegistryEntry[attributeMap.size()];
		
		int index = 0;
		for (String value : attributeMap.values()) {
			tests[index] = new RegistryEntry(value, null);
			index++;
		}		
		return tests;	
	}
}
