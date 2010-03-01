package com.stabilit.sc.app.server;

import com.stabilit.sc.app.Application;
import com.stabilit.sc.app.server.handler.IKeepAliveHandler;
import com.stabilit.sc.context.ServerApplicationContext;
import com.stabilit.sc.msg.ISCServiceListener;

public abstract class ServerApplication extends Application {

	public ServerApplication() {
		super(new ServerApplicationContext());
	}

	public int getPort() {
		return 8066;
	}

	public abstract void create(Class<? extends ISCServiceListener> scListenerClass,
			Class<? extends IKeepAliveHandler> keepAliveHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout);
}
