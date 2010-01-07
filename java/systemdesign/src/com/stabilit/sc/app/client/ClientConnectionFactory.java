package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.http.SunHttpClient;
import com.stabilit.sc.app.client.socket.http.SocketHttpClient;
import com.stabilit.sc.app.server.socket.http.SocketHttpServer;

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
		clientMap.put(SocketHttpServer.class.getName(), socketHttpClient);
		clientMap.put("socket.http", socketHttpClient);
	}
	
	public static IClient newInstance() {
		return newInstance("default");
	}

	public static IClient newInstance(String key) {
		IClient client = clientMap.get(key);
		return client;
	}

}
