package com.stabilit.sc.app.server;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.responder.netty.http.NettyHttpResponder;
import com.stabilit.sc.app.responder.netty.tcp.NettyTcpResponder;
import com.stabilit.sc.util.ConsoleUtil;

public class ServerApplicationFactory {

	private static Map<String, IApplication> serverMap = new HashMap<String, IApplication>();

	static {
		// jboss netty http server
		IApplication nettyHttpServer = new NettyHttpResponder();
		serverMap.put(NettyHttpResponder.class.getName(), nettyHttpServer);
		serverMap.put("netty.http", nettyHttpServer);
		
		// jboss netty tcp server
		IApplication nettyTCPServer = new NettyTcpResponder();
		serverMap.put(NettyTcpResponder.class.getName(), nettyTCPServer);
		serverMap.put("netty.tcp", nettyTCPServer);
	}
	
	public static IApplication newInstance() {
		return newInstance("default");
	}

	public static IApplication newInstance(String key) {
		IApplication server = serverMap.get(key);
		return server;
	}

	public static Object[] getApplications() {
		return serverMap.keySet().toArray();
	}
	
	public static String getApplicationKey(String[]args) {
		return ConsoleUtil.getArg(args, "-app");
	}
}
