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
package com.stabilit.sc.unit.test;

import org.junit.After;
import org.junit.Before;

import com.stabilit.sc.cln.client.ClientFactory;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;

/**
 * @author JTraber
 * 
 */
public class SuperTestCase {

	protected ClientConfig config = null;
	protected IClient client = null;

	public SuperTestCase() {
		super();
	}

	@Before
	public void setup() throws Exception {
		SetupTestCases.setupAll();
		try {
			config = new ClientConfig();
			config.load("sc-unit.properties");
			ClientFactory clientFactory = new ClientFactory();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		client.disconnect();
		//client.destroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		client.disconnect(); // physical disconnect
		client.destroy();
		ConnectionListenerSupport.getInstance().clearAll();
		client = null;
	}
}