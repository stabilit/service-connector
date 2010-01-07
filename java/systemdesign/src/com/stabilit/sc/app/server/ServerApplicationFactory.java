package com.stabilit.sc.app.server;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.server.mina.http.MinaHttpServer;
import com.stabilit.sc.app.server.socket.http.SocketHttpServer;
import com.stabilit.sc.app.server.sun.net.http.SunHttpServer;

public class ServerApplicationFactory {

	private static Map<String, IApplication> serverMap = new HashMap<String, IApplication>();

	static {
		// sun net http server
		IApplication httpServer = new SunHttpServer();
		serverMap.put(SunHttpServer.class.getName(), httpServer);
		serverMap.put("sun.http", httpServer);
		serverMap.put("default", httpServer);
		// stabilit socket http server
		IApplication socketHttpServer = new SocketHttpServer(); 
		serverMap.put(SocketHttpServer.class.getName(), socketHttpServer);
		serverMap.put("socket.http", socketHttpServer);
		// mina apache http server
		IApplication minaHttpServer = new MinaHttpServer();
		serverMap.put(MinaHttpServer.class.getName(), minaHttpServer);
		serverMap.put("mina.http", minaHttpServer);
	}
	
	public static IApplication newInstance() {
		return newInstance("default");
	}

	public static IApplication newInstance(String key) {
		IApplication server = serverMap.get(key);
		return server;
	}

}
