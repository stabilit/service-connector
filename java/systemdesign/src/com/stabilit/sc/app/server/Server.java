package com.stabilit.sc.app.server;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.context.ServerApplicationContext;

public class Server {
	public static void main(String[] args) throws ServerException {
		String key = "default";
		if (args.length <= 0) {
			System.err.println("no arguments");
			printUsage();
			System.exit(1);
		}
		key = ServerApplicationFactory.getApplicationKey(args);
		IApplication application = ServerApplicationFactory.newInstance(key);
		if (application == null) {
			System.err.println("no application found for given key = "	+ key);
			printUsage();
			System.exit(1);
		}
		ServerApplicationContext applicationContext = (ServerApplicationContext) application.getContext();
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);
		try {
		   applicationContext.setArgs(arguments);
		} catch(Exception e) {			
			System.err.println(e.toString());
			printUsage();
			System.exit(1);
		}
		try {
			System.out.println("starting up server for app = " + key + " on port = " + applicationContext.getPort());	
			application.create();
			System.out.println("run server for key = " + key + " on port = " + applicationContext.getPort());
			application.run();
			application.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printUsage() {
		System.out.println("\nUsage: java -jar Server.jar -app <application> -port <ip port>");
		Object[] applications = (Object[]) ServerApplicationFactory.getApplications();
		System.out.print("  Available applications: ");
		boolean first = true;
		for (Object app : applications) {
			if (((String)app).indexOf("stabilit") >= 0) {
				continue;
			}
			if (((String)app).indexOf("default") >= 0) {
				continue;
			}
			if (first == false) {
				System.out.print(", ");
			}
			System.out.print(app);
			first = false;
		}
		System.out.println("\n\nExample: java -jar Server.jar -app netty.http -port 80");
	}
}
