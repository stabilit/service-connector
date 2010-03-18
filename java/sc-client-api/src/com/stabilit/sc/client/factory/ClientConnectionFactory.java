package com.stabilit.sc.client.factory;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.net.client.netty.http.NettyHttpClientConnection;
import com.stabilit.sc.net.client.netty.tcp.NettyTcpClientConnection;

public class ClientConnectionFactory extends Factory {

	public ClientConnectionFactory() {
		// jboss netty http server
		IClientConnection nettyHttpClient = new NettyHttpClientConnection();
		add("default", nettyHttpClient);
		add("netty.http", nettyHttpClient);
		// jboss netty tcp server
		IClientConnection nettyTCPCient = new NettyTcpClientConnection();
		add("netty.tcp", nettyTCPCient);
	}

	public IClientConnection newInstance() {
		return newInstance("default");
	}

	public IClientConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IClientConnection) factoryInstance; // should be a clone if implemented
	}
}
