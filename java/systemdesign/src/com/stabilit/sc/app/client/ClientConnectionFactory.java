package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.jboss.netty.http.NettyHttpClient;
import com.stabilit.sc.app.client.mina.http.MinaHttpClient;
import com.stabilit.sc.app.client.socket.http.SocketHttpClient;
import com.stabilit.sc.app.client.sun.http.SunHttpClient;

public class ClientConnectionFactory {

	private static Map<String, IClient> clientMap = new HashMap<String, IClient>();

	static {
		// sun net http client
		IClient httpClient = new SunHttpClient();
		clientMap.put(SunHttpClient.class.getName(), httpClient);
		clientMap.put("sun.http", httpClient);
		clientMap.put("default", httpClient);
		// stabilit socket http client
		IClient socketHttpClient = new SocketHttpClient(); 
		clientMap.put(SocketHttpClient.class.getName(), socketHttpClient);
		clientMap.put("socket.http", socketHttpClient);
		// mina http client
		IClient minaHttpClient = new MinaHttpClient(); 
		clientMap.put(MinaHttpClient.class.getName(), minaHttpClient);
		clientMap.put("mina.http", minaHttpClient);
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
