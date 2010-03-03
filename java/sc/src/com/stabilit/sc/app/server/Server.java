package com.stabilit.sc.app.server;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.context.ServerApplicationContext;
import com.stabilit.sc.exception.ServerException;

public class Server implements Runnable {

	private String[] args;
	private Properties props;
	private Logger log = Logger.getLogger(Server.class);

	public static void main(String[] args) throws ServerException {
		Server server = new Server();
		server.setArgs(args);
		server.run();
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	@Override
	public void run() {
		String key = props.getProperty("server.app");

		IApplication application = ServerApplicationFactory.newInstance(key);

		if (application == null) {
			log.error("no application found for given key = " + key);
			System.exit(1);
		}
		ServerApplicationContext applicationContext = (ServerApplicationContext) application.getContext();
		applicationContext.setProps(props);

		try {
			log.info("starting up server for app = " + key + " on port = " + applicationContext.getPort());
			application.create();
			log.info("run server for key = " + key + " on port = " + applicationContext.getPort());
			application.run();
			application.destroy();
		} catch (Exception e) {
			log.error("Error when applications creates/runs or destroys");
		}
	}

	public static void printUsageOld() {
		System.out.println("\nUsage: java -jar Server.jar -app <application> -port <ip port>");
		Object[] applications = (Object[]) ServerApplicationFactory.getApplications();
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
		System.out.println("\n\nExample: java -jar Server.jar -app netty.http -port 80");
	}

	public void runOld() {
		String key = "default";
		if (args.length <= 0) {
			System.err.println("no arguments");
			printUsageOld();
			System.exit(1);
		}
		key = ServerApplicationFactory.getApplicationKey(args);
		IApplication application = ServerApplicationFactory.newInstance(key);
		if (application == null) {
			System.err.println("no application found for given key = " + key);
			printUsageOld();
			System.exit(1);
		}
		ServerApplicationContext applicationContext = (ServerApplicationContext) application.getContext();
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);
		try {
			applicationContext.setArgs(arguments);
		} catch (Exception e) {
			System.err.println(e.toString());
			printUsageOld();
			System.exit(1);
		}
		try {
			System.out.println("starting up server for app = " + key + " on port = "
					+ applicationContext.getPort());
			application.create();
			System.out.println("run server for key = " + key + " on port = " + applicationContext.getPort());
			application.run();
			application.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
