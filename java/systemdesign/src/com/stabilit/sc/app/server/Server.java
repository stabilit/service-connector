package com.stabilit.sc.app.server;


public class Server {
	public static void main(String[] args) throws ServerException {
		String key = "default";
		if (args.length > 0) {
			key = args[0];
		}
		// use default server		
		IServer server = ServerFactory.newInstance(key);
		try {
			if (server == null) {
				throw new ServerException("no server available");
			}
			System.out.println("starting up server for key = " + key);
			server.create();
			System.out.println("run server for key = " + key);
			server.run();
			server.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
