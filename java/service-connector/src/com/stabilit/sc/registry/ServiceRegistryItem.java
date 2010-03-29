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
package com.stabilit.sc.registry;

import java.util.Map;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.service.SCMPAllocateSessionCall;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPDeAllocateSessionCall;
import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public class ServiceRegistryItem extends MapBean<String> {

	private IClient client;

	public ServiceRegistryItem() {
		ClientFactory clientFactory = new ClientFactory();
		//TODO clientConfig problem
		ClientConfigItem config = new ClientConfig().new ClientConfigItem();
	}

	public void allocate(Map<String, String> attrMap) throws Exception {
		SCMPAllocateSessionCall allocateSessionCall = (SCMPAllocateSessionCall) SCMPCallFactory.ALLOCATE_SESSION_CALL
				.newInstance(client);
		allocateSessionCall.setHeader(attrMap);
		allocateSessionCall.invoke();
	}

	public void deallocate(Map<String, String> attrMap) throws Exception {
		SCMPDeAllocateSessionCall deAllocateSessionCall = (SCMPDeAllocateSessionCall) SCMPCallFactory.DEALLOCATE_SESSION_CALL
				.newInstance(client);
		deAllocateSessionCall.setHeader(attrMap);
		deAllocateSessionCall.invoke();
	}

	public boolean isAllocated() {
		return false;
	}
}
