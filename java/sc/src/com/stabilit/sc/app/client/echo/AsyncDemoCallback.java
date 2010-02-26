package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.Callback;
import com.stabilit.sc.pool.IPoolConnection;

public class AsyncDemoCallback extends Callback {


	public AsyncDemoCallback(IPoolConnection con) {
		super(con);
	}
	
	@Override
	public void callback(SCMP scmp) throws Exception {
		super.callback(scmp);
		System.out.println("AsyncDemoCallback.callback()");
		Object obj = scmp.getBody();
		System.out.println(obj);
		this.sendAsyncRequest();
	}

}
