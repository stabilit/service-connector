package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.netty.http.NettyHttpClient;

public class ClientConnectionFactory {

	private static Map<String, IClient> clientMap = new HashMap<String, IClient>();

	static {
		// jboss netty http client
		IClient nettyHttpClient = new NettyHttpClient(); 
		clientMap.put(NettyHttpClient.class.getName(), nettyHttpClient);
		clientMap.put("netty.http", nettyHttpClient);
	}
	
	public static IClient newInstance() {
		return newInstance("default");
	}

	public static IClient newInstance(String key) {
		IClient client = clientMap.get(key);
		return client;
	}

}
