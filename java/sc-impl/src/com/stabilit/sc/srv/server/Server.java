package com.stabilit.sc.srv.server;

import com.stabilit.sc.srv.config.IServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.ctx.ServerContext;
import com.stabilit.sc.srv.server.factory.ServerConnectionFactory;

/**
 * @author JTraber
 * 
 */
public abstract class Server implements IServer {

	private IServerConfigItem serverConfig;
	private IServerConnection serverConnection;
	protected IServerContext serverContext;	

	@Override
	public void setServerConfig(IServerConfigItem serverConfig) {
		this.serverConfig = serverConfig;
		this.serverContext = new ServerContext(this);
		ServerConnectionFactory serverConnectionFactory = new ServerConnectionFactory();
		this.serverConnection = serverConnectionFactory.newInstance(this.serverConfig.getCon());
		this.serverConnection.setServer(this);
		this.serverConnection.setHost(this.serverConfig.getHost());
		this.serverConnection.setPort(this.serverConfig.getPort());
	}

	@Override
	public void create() throws Exception {		
		serverConnection.create();
	}

	@Override
	public void runAsync() throws Exception {
		serverConnection.runAsync();
	}
	
	@Override
	public void runSync() throws Exception {
		serverConnection.runSync();
	}

	@Override
	public IServerContext getServerContext() {
		return serverContext;
	}

	@Override
	public IServerConfigItem getServerConfig() {
		return serverConfig;
	}
}
