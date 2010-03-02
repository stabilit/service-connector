package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.netty.http.NettyHttpConnection;
import com.stabilit.sc.app.client.netty.tcp.NettyTCPConnection;

public class ClientConnectionFactory {

	private static Map<String, IClientConnection> clientMap = new HashMap<String, IClientConnection>();

	static {
		// jboss netty http client
		IClientConnection nettyHttpClient = new NettyHttpConnection(); 
		clientMap.put(NettyHttpConnection.class.getName(), nettyHttpClient);
		clientMap.put("netty.http", nettyHttpClient);
		
		// jboss netty tcp client
		IClientConnection nettyTCPConnection = new NettyTCPConnection(); 
		clientMap.put(NettyTCPConnection.class.getName(), nettyTCPConnection);
		clientMap.put("netty.tcp", nettyTCPConnection);
	}
	
	public static IClientConnection newInstance() {
		return newInstance("default");
	}

	public static IClientConnection newInstance(String key) {
		IClientConnection client = clientMap.get(key);
		return client;
	}

}
