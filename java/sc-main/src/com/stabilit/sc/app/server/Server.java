package com.stabilit.sc.app.server;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.stabilit.sc.context.ServerConnectionContext;
import com.stabilit.sc.server.IServerConnection;

public class Server implements Runnable {

	private Properties props;
	private Logger log = Logger.getLogger(Server.class);

	public void setProps(Properties props) {
		this.props = props;
	}

	@Override
	public void run() {

		ServerConnectionContext serverCtx = new ServerConnectionContext(props.getProperty("host"),
				new Integer(props.getProperty("server.port")), props.getProperty("server.connectionType"));
		IServerConnection con = serverCtx.create();

		try {
			con.run();
			con.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
