package com.stabilit.scm.cln.service;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.service.IServiceConnector;

public interface IServiceContext extends IContext {

	public abstract IServiceConnector getServiceConnector();
	
	public abstract IService getService();
}
