package com.stabilit.sc.app.client;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.context.IApplicationContext;

public class Client {

	public static void main(String[] args) {
		if (args.length <= 0) {
			System.err.println("no application key defined");
			System.exit(1);
		}
		IApplication application = ClientApplicationFactory.newInstance(args[0]);
		if (application == null) {
			System.err.println("no application found for given key = " + args[0]);
			System.exit(1);
		}
		IApplicationContext applicationContext = application.getContext();
		String []arguments = new String[args.length-1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);
		applicationContext.setArgs(arguments);
		
		try {
			application.create();
			application.run();
			application.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
