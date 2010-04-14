package com.stabilit.sc.srv.server.factory;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.srv.net.server.netty.tcp.NettyTcpServerConnection;
import com.stabilit.sc.srv.net.server.nio.http.NioHttpServer;
import com.stabilit.sc.srv.net.server.nio.tcp.NioTcpServer;
import com.stabilit.sc.srv.server.IServerConnection;

public class ServerConnectionFactory extends Factory {

	public ServerConnectionFactory() {
		// jboss netty http server
		IServerConnection nettyHttpServer = new NettyHttpServerConnection();
		add("default", nettyHttpServer);
		add("netty.http", nettyHttpServer);
		// jboss netty tcp server
		IServerConnection nettyTCPServer = new NettyTcpServerConnection();
		add("netty.tcp", nettyTCPServer);
		
		// nioServer
		IServerConnection nioTcpServer = new NioTcpServer();
		add("nio.tcp", nioTcpServer);
		
		// nioServer
		IServerConnection nioHttpServer = new NioHttpServer();
		add("nio.http", nioHttpServer);
	}

	public IServerConnection newInstance() {
		return newInstance("default");
	}

	public IServerConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IServerConnection) factoryInstance; // should be a clone if implemented
	}

}
