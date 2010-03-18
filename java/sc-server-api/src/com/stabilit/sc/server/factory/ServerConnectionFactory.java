package com.stabilit.sc.server.factory;

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.net.server.netty.tcp.NettyTcpServerConnection;
import com.stabilit.sc.server.IServerConnection;

public class ServerConnectionFactory extends Factory {

	public ServerConnectionFactory() {
		// jboss netty http server
		IServerConnection nettyHttpServer = new NettyHttpServerConnection();
		add("default", nettyHttpServer);
		add("netty.http", nettyHttpServer);
		// jboss netty tcp server
		IServerConnection nettyTCPServer = new NettyTcpServerConnection();
		add("netty.tcp", nettyTCPServer);
	}

	public IServerConnection newInstance() {
		return newInstance("default");
	}

	public IServerConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IServerConnection) factoryInstance; // should be a clone if implemented
	}

}
