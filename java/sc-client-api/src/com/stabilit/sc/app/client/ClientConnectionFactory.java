package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.net.client.netty.http.NettyHttpClientConnection;
import com.stabilit.sc.net.client.netty.tcp.NettyRegisterTcpClientConnection;
import com.stabilit.sc.net.client.netty.tcp.NettyTcpClientConnection;

public class ClientConnectionFactory {

	private static Map<String, Class<? extends IClientConnection>> clientMap = new HashMap<String, Class<? extends IClientConnection>>();

	static {
		clientMap.put("netty.http", NettyHttpClientConnection.class);

		clientMap.put("netty.tcp", NettyTcpClientConnection.class);

		clientMap.put("nettyRegister.tcp", NettyRegisterTcpClientConnection.class);
	}

	public static IClientConnection newInstance(String key) {
		IClientConnection client = null;
		//TODO correct exception handling
		try {
			client = clientMap.get(key).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return client;
	}

}
