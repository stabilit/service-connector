package com.stabilit.sc.context;

import com.stabilit.sc.util.ConsoleUtil;

public class ServerApplicationContext extends ApplicationContext {

	public ServerApplicationContext() {
	}

	public int getPort() {
		Integer port = (Integer) this.getAttribute("port");
		if (port == null) {
			return 80;
		}
		return port;
	}

	public void setArgs(String[] args) throws Exception {
		super.setArgs(args);
		String sPort = ConsoleUtil.getArg(args, "-port");
		int port = 80;
		try {
			port = Integer.parseInt(sPort);
		} catch (NumberFormatException e) {
		}
		this.setAttribute("port", port);
	}
}
