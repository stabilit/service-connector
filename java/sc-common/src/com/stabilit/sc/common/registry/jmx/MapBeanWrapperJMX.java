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
package com.stabilit.sc.common.registry.jmx;

import java.beans.ConstructorProperties;

import com.stabilit.sc.common.util.MapBean;

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
