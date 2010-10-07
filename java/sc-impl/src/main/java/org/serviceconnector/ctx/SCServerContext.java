package org.serviceconnector.ctx;

import org.serviceconnector.api.srv.SrvServiceRegistry;

public class SCServerContext extends AppContext {

	private static final SrvServiceRegistry srvServiceRegistry = new SrvServiceRegistry();

	private SCServerContext() {
	}

	public static SCServerContext getCurrentContext() {
		if (AppContext.instance == null) {
			AppContext.instance = new SCServerContext();
		}
		return (SCServerContext) AppContext.instance;
	}

	public SrvServiceRegistry getSrvServiceRegistry() {
		return SCServerContext.srvServiceRegistry;
	}
}
