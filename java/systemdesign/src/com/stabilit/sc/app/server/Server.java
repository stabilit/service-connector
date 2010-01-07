package com.stabilit.sc.app.server;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.context.IApplicationContext;


public class Server {
	public static void main(String[] args) throws ServerException {
		String key = "default";
		if (args.length > 0) {
			key = args[0];
		}
		IApplication application = ServerApplicationFactory.newInstance(args[0]);
		if (application == null) {
			System.err.println("no application found for given key = " + args[0]);
		}
		IApplicationContext applicationContext = application.getContext();
		String []arguments = new String[args.length-1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);
		applicationContext.setArgs(arguments);
		try {
			System.out.println("starting up server for key = " + key);
			application.create();
			System.out.println("run server for key = " + key);
			application.run();
			application.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
