package com.stabilit.scm.cln.service;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.service.ISC;

public interface IServiceContext extends IContext {

	public abstract ISC getServiceConnector();
	
	public abstract IService getService();
}
