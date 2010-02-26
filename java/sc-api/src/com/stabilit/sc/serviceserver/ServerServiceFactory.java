package com.stabilit.sc.serviceserver;

import java.util.HashMap;
import java.util.Map;


public class ServerServiceFactory {

	private static Map<String, IServiceServer> serviceServerMap = new HashMap<String, IServiceServer>();

	static {
		IServiceServer nettyServiceServer = new NettyHttpReqResServiceServer("localhost",80);
		serviceServerMap.put(NettyHttpReqResServiceServer.class.getName(), nettyServiceServer);
		serviceServerMap.put("nettyServiceServer.reqRes.http", nettyServiceServer);
	}
	
	public static IServiceServer newInstance() {
		return newInstance("default");
	}

	public static IServiceServer newInstance(String key) {
		IServiceServer serviceServer = serviceServerMap.get(key);
		return serviceServer;
	}

	public static Object[] getServerService() {
		return serviceServerMap.keySet().toArray();
	}
}
