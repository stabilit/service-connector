/*
 *-----------------------------------------------------------------------------*
 *                            Copyright � 2010 by                              *
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
package com.stabilit.sc.server;

import com.stabilit.sc.conf.ServerConfig;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.server.factory.ServerConnectionFactory;

/**
 * @author JTraber
 * 
 */
public class Server implements IServer {

	private ServerConfig serverConfig;
	private IServerConnection serverConnection;
	

	@Override
	public IFactoryable newInstance() {
		return new Server();
	}

	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		ServerConnectionFactory serverConnectionFactory = new ServerConnectionFactory();
		this.serverConnection = serverConnectionFactory.newInstance(this.serverConfig.getCon());
		this.serverConnection.setHost(this.serverConfig.getHost());
		this.serverConnection.setPort(this.serverConfig.getPort());
	}

	@Override
	public void create() {		
		serverConnection.create();
	}

	@Override
	public void runAsync() {
		serverConnection.runAsync();
	}

	@Override
	public void runSync() throws InterruptedException {
		serverConnection.runSync();
	}
}
