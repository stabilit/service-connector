package com.stabilit.sc.app;

import com.stabilit.sc.context.ApplicationContext;
import com.stabilit.sc.context.IApplicationContext;

public abstract class Application implements IApplication {

	private IApplicationContext applicationContext;

	public Application() {
		this.applicationContext = new ApplicationContext();
	}
	
	public Application(IApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public IApplicationContext getContext() {
		return applicationContext;
	}

}
