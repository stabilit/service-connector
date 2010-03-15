package com.stabilit.sc.app.server;

import com.stabilit.sc.app.Application;
import com.stabilit.sc.context.ServerApplicationContext;


public abstract class ServerApplication extends Application {

	public ServerApplication() {
		super(new ServerApplicationContext());
	}

	public int getPort() {
		return 8066;
	}
}
