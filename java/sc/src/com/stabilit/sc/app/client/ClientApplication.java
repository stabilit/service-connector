package com.stabilit.sc.app.client;

import com.stabilit.sc.app.Application;
import com.stabilit.sc.context.ClientApplicationContext;

public abstract class ClientApplication extends Application {

	public ClientApplication() {
		super(new ClientApplicationContext());
	}
	
	@Override
	public void create() throws Exception {		
	}
	
	@Override
	public void destroy() throws Exception {		
	}
	
}
