package com.stabilit.scm.cln.service;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.service.IServiceConnector;

public interface ISessionContext extends IContext {

	public abstract IServiceConnector getServiceConnector();
	
	public abstract ISessionService getSessionService();
}
