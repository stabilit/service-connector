package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.jboss.netty.http.NettyHttpConnection;
import com.stabilit.sc.app.client.mina.http.MinaHttpConnection;
import com.stabilit.sc.app.client.socket.http.SocketHttpConnection;
import com.stabilit.sc.app.client.sun.http.SunHttpConnection;

public class ClientConnectionFactory {

	private static Map<String, IConnection> clientMap = new HashMap<String, IConnection>();

	static {
		// sun net http client
		IConnection httpClient = new SunHttpConnection();
		clientMap.put(SunHttpConnection.class.getName(), httpClient);
		clientMap.put("sun.http", httpClient);
		clientMap.put("default", httpClient);
		// stabilit socket http client
		IConnection socketHttpClient = new SocketHttpConnection(); 
		clientMap.put(SocketHttpConnection.class.getName(), socketHttpClient);
		clientMap.put("socket.http", socketHttpClient);
		// mina http client
		IConnection minaHttpClient = new MinaHttpConnection(); 
		clientMap.put(MinaHttpConnection.class.getName(), minaHttpClient);
		clientMap.put("mina.http", minaHttpClient);
		// jboss netty http client
		IConnection nettyHttpClient = new NettyHttpConnection(); 
		clientMap.put(NettyHttpConnection.class.getName(), nettyHttpClient);
		clientMap.put("netty.http", nettyHttpClient);
	}
	
	public static IConnection newInstance() {
		return newInstance("default");
	}

	public static IConnection newInstance(String key) {
		IConnection client = clientMap.get(key);
		return client;
	}

}
