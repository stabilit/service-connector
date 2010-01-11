package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.client.echo.AsyncDemoClientApplication;
import com.stabilit.sc.app.client.echo.EchoClientApplication;
import com.stabilit.sc.app.client.echo.EchoClientApplicationKeepAlive;

public class ClientApplicationFactory {

	private static Map<String, IApplication> appMap = new HashMap<String, IApplication>();

	static {
		IApplication echoClient = new EchoClientApplication();
		appMap.put(EchoClientApplication.class.getName(), echoClient);
		appMap.put("echo.client", echoClient);
		IApplication echoClientKeepAlive = new EchoClientApplicationKeepAlive();
		appMap.put(EchoClientApplicationKeepAlive.class.getName(), echoClientKeepAlive);
		appMap.put("echo.client.keep.alive", echoClientKeepAlive);
		IApplication asyncDemoClient = new AsyncDemoClientApplication();
		appMap.put(AsyncDemoClientApplication.class.getName(), asyncDemoClient);
		appMap.put("async.demo", asyncDemoClient);
	}
	
	public static IApplication newInstance(String key) {
		IApplication app = appMap.get(key);
		return app;
	}

}
