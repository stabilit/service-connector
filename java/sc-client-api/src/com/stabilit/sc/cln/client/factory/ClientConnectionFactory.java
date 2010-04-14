package com.stabilit.sc.cln.client.factory;

import com.stabilit.sc.cln.client.IClientConnection;
import com.stabilit.sc.cln.net.client.netty.http.NettyHttpClientConnection;
import com.stabilit.sc.cln.net.client.netty.tcp.NettyTcpClientConnection;
import com.stabilit.sc.cln.net.client.nio.http.NioHttpClientConnection;
import com.stabilit.sc.cln.net.client.nio.tcp.NioTcpClientConnection;
import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;

public class ClientConnectionFactory extends Factory {

	public ClientConnectionFactory() {
		// jboss netty http server
		IClientConnection nettyHttpClient = new NettyHttpClientConnection();
		add("default", nettyHttpClient);
		add("netty.http", nettyHttpClient);
		// jboss netty tcp server
		IClientConnection nettyTCPClient = new NettyTcpClientConnection();
		add("netty.tcp", nettyTCPClient);
		
		IClientConnection nioTCPClient = new NioTcpClientConnection();
		add("nio.tcp", nioTCPClient); 
		
		IClientConnection nioHttpClient = new NioHttpClientConnection();
		add("nio.http", nioHttpClient); 
	}

	public IClientConnection newInstance() {
		return newInstance("default");
	}

	public IClientConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IClientConnection) factoryInstance; // should be a clone if implemented
	}
}
