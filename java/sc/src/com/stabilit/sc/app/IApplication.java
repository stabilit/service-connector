package com.stabilit.sc.app;

import com.stabilit.sc.context.IApplicationContext;

public interface IApplication {

	public IApplicationContext getContext();
	
	public void create() throws Exception;
	
	public void run() throws Exception;

	public void destroy() throws Exception;

}
