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

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.srv.server.IServer;
import com.stabilit.sc.srv.server.Server;
import com.stabilit.sc.srv.server.ServerFactory;

/**
 * @author JTraber
 *
 */
public class SimluationServerFactory extends ServerFactory {
	public SimluationServerFactory() {
	    Server server = new SimluationServer();
	    this.factoryMap.put("default", server);
	}
	
	public IServer newInstance(ServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IServer server = (IServer)factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}

}
