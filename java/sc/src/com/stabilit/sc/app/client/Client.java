package com.stabilit.sc.app.client;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.server.ServerApplicationFactory;
import com.stabilit.sc.context.ClientApplicationContext;

public class Client implements Runnable {

	private String[] args;
	
	public static void main(String[] args) {
		Client client = new Client();
		client.setArgs(args);
		client.run();
	}

	public static void printUsage() {
		System.out.println("\nUsage: java -jar Client.jar -app <application> -con <Connection> -url <url>");
		Object[] applications = (Object[]) ClientApplicationFactory.getApplications();
		System.out.print("  Available applications: ");
		boolean first = true;
		for (Object app : applications) {
			if (((String) app).indexOf("stabilit") >= 0) {
				continue;
			}
			if (((String) app).indexOf("default") >= 0) {
				continue;
			}
			if (first == false) {
				System.out.print(", ");
			}
			System.out.print(app);
			first = false;
		}
		System.out
				.println("\n\nExample: java -jar Client.jar -app echo.client -con netty.http -url http://localhost:8080");
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public void run() {
		if (args.length <= 0) {
			System.err.println("no arguments");
			printUsage();
			System.exit(1);
		}
		String key = ServerApplicationFactory.getApplicationKey(args);
		IApplication application = ClientApplicationFactory.newInstance(key);
		if (application == null) {
			System.err.println("no application found for given key = " + key);
			printUsage();
			System.exit(1);
		}
		ClientApplicationContext applicationContext = (ClientApplicationContext) application.getContext();
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);
		try {
			applicationContext.setArgs(arguments);
		} catch (Exception e) {
			System.err.println(e.toString());
			printUsage();
			System.exit(1);
		}
		try {
			application.create();
			application.run();
			application.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
