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
package com.stabilit.sc.sim.server;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.server.Server;
import com.stabilit.sc.service.ISCMPCall;
import com.stabilit.sc.service.SCMPCallFactory;

/**
 * @author JTraber
 * 
 */
public class SimluationServer extends Server {
	private ClientFactory clientFactory;
	private IClient client;

	public SimluationServer() {
		clientFactory = new ClientFactory();
		client = null;
	}

	@Override
	public void create() throws Exception {
		super.create();
		ClientConfig clientConfig = (ClientConfig) this.getServerContext().getAttribute(ClientConfig.class.getName());
		client = clientFactory.newInstance(clientConfig.getClientConfig());
		client.connect(); // physical connect
		// scmp registerService		
		ISCMPCall registerService = SCMPCallFactory.REGISTER_SERVICE_CALL.newInstance(client);
		registerService.invoke();
	}

	@Override
	public IFactoryable newInstance() {
		return new SimluationServer();
	}
}
