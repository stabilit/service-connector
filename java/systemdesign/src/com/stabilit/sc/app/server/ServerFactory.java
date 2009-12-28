package com.stabilit.sc.app.server;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.server.mina.http.MinaHttpServer;
import com.stabilit.sc.app.server.socket.http.SocketHttpServer;
import com.stabilit.sc.app.server.sun.net.http.SunHttpServer;

public class ServerFactory {

	private static Map<String, IServer> serverMap = new HashMap<String, IServer>();

	static {
		// sun net http server
		IServer httpServer = new SunHttpServer();
		serverMap.put(SunHttpServer.class.getName(), httpServer);
		serverMap.put("sun.http", httpServer);
		serverMap.put("default", httpServer);
		// stabilit socket http server
		IServer socketHttpServer = new SocketHttpServer(); 
		serverMap.put(SocketHttpServer.class.getName(), socketHttpServer);
		serverMap.put("socket.http", socketHttpServer);
		// mina apache http server
		IServer minaHttpServer = new MinaHttpServer();
		serverMap.put(MinaHttpServer.class.getName(), minaHttpServer);
		serverMap.put("mina.http", minaHttpServer);
	}
	
	public static IServer newInstance() {
		return newInstance("default");
	}

	public static IServer newInstance(String key) {
		IServer server = serverMap.get(key);
		return server;
	}

}
