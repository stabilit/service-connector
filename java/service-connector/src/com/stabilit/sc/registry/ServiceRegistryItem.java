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

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.MapBean;

/**
 * @author JTraber
 * 
 */
public class ServiceRegistryItem extends MapBean<String> {

	private SCMP scmp;
	private IClient client;

	public ServiceRegistryItem(SCMP scmp) {
		this.scmp = scmp;
		this.attrMap = scmp.getHeader();
	}

	public void allocate() throws Exception {
         // TODO allocate session call
		
	}

	public void deallocate() throws Exception {
        // TODO deallocate session
	}

	public boolean isAllocated() {
       return false;
	}
}
