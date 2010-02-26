package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.client.echo.EchoClientApplication;

public class ClientApplicationFactory {

	private static Map<String, IApplication> clientMap = new HashMap<String, IApplication>();

	static {
		IApplication echoClient = new EchoClientApplication();
		clientMap.put(EchoClientApplication.class.getName(), echoClient);
		clientMap.put("echo.client", echoClient);
	}
	
	public static IApplication newInstance(String key) {
		IApplication app = clientMap.get(key);
		return app;
	}

	public static Object[] getApplications() {
		return clientMap.keySet().toArray();
	}

}
