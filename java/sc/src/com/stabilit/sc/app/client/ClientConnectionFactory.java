package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.netty.http.NettyHttpConnection;

public class ClientConnectionFactory {

	private static Map<String, IClientConnection> clientMap = new HashMap<String, IClientConnection>();

	static {
		// jboss netty http client
		IClientConnection nettyHttpClient = new NettyHttpConnection(); 
		clientMap.put(NettyHttpConnection.class.getName(), nettyHttpClient);
		clientMap.put("netty.http", nettyHttpClient);
	}
	
	public static IClientConnection newInstance() {
		return newInstance("default");
	}

	public static IClientConnection newInstance(String key) {
		IClientConnection client = clientMap.get(key);
		return client;
	}

}
