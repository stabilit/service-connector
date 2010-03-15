package com.stabilit.sc.app.server;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.net.server.netty.tcp.NettyTcpServerConnection;
import com.stabilit.sc.server.IServerConnection;

public class ServerApplicationFactory {

	private static Map<String, IServerConnection> serverMap = new HashMap<String, IServerConnection>();

	static {
		// jboss netty http server
		IServerConnection nettyHttpServer = new NettyHttpServerConnection();
		serverMap.put(NettyHttpServerConnection.class.getName(), nettyHttpServer);
		serverMap.put("netty.http", nettyHttpServer);
		
		// jboss netty tcp server
		IServerConnection nettyTCPServer = new NettyTcpServerConnection();
		serverMap.put(NettyTcpServerConnection.class.getName(), nettyTCPServer);
		serverMap.put("netty.tcp", nettyTCPServer);
	}
	
	public static IServerConnection newInstance() {
		return newInstance("default");
	}

	public static IServerConnection newInstance(String key) {
		IServerConnection server = serverMap.get(key);
		return server;
	}

	public static Object[] getApplications() {
		return serverMap.keySet().toArray();
	}
	
}
