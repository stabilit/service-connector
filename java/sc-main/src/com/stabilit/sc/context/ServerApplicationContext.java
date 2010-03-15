package com.stabilit.sc.context;

import com.stabilit.sc.util.ConsoleUtil;


public class ServerApplicationContext extends ApplicationContext {

	public ServerApplicationContext() {
	}

	public int getPort() {
		Integer port = Integer.valueOf((String) this.getAttribute("server.port"));
		if (port == null) {
			return 8066;
		}
		return port;
	}

	public void setArgs(String[] args) throws Exception {
		super.setArgs(args);
		String sPort = ConsoleUtil.getArg(args, "-port");
		int port = 8066;
		try {
			port = Integer.parseInt(sPort);
		} catch (NumberFormatException e) {
		}
		this.setAttribute("port", port);
	}
}
