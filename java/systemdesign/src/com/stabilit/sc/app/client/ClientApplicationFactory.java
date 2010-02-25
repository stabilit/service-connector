package com.stabilit.sc.app.client;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.client.echo.AsyncDemoClientApplication;
import com.stabilit.sc.app.client.echo.EchoClientApplication;
import com.stabilit.sc.app.client.echo.EchoClientApplicationKeepAlive;
import com.stabilit.sc.app.client.echo.EchoStringClientApplication;
import com.stabilit.sc.app.client.performance.PerformanceApplication;

public class ClientApplicationFactory {

	private static Map<String, IApplication> clientMap = new HashMap<String, IApplication>();

	static {
		IApplication echoClient = new EchoClientApplication();
		clientMap.put(EchoClientApplication.class.getName(), echoClient);
		clientMap.put("echo.client", echoClient);
		IApplication echoStringClient = new EchoStringClientApplication();
		clientMap.put(EchoStringClientApplication.class.getName(), echoStringClient);
		clientMap.put("echo.string.client", echoStringClient);
		IApplication echoClientKeepAlive = new EchoClientApplicationKeepAlive();
		clientMap.put(EchoClientApplicationKeepAlive.class.getName(), echoClientKeepAlive);
		clientMap.put("echo.client.keep.alive", echoClientKeepAlive);
		IApplication asyncDemoClient = new AsyncDemoClientApplication();
		clientMap.put(AsyncDemoClientApplication.class.getName(), asyncDemoClient);
		clientMap.put("async.demo", asyncDemoClient);
		IApplication performanceApplication = new PerformanceApplication();
		clientMap.put(PerformanceApplication.class.getName(), performanceApplication);
		clientMap.put("perf.client", performanceApplication);
	}
	
	public static IApplication newInstance(String key) {
		IApplication app = clientMap.get(key);
		return app;
	}

	public static Object[] getApplications() {
		return clientMap.keySet().toArray();
	}

}
