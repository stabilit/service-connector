package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.Callback;

public class AsyncDemoCallback extends Callback {


	public AsyncDemoCallback(IConnection con) {
		super(con);
	}
	
	@Override
	public void callback(SCMP scmp) throws Exception {
		System.out.println("AsyncDemoCallback.callback()");
		Object obj = scmp.getBody();
		System.out.println(obj);
		this.sendAsyncRequest();
	}

}
