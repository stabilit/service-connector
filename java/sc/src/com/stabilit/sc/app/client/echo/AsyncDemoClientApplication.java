package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.exception.ServerException;
import com.stabilit.sc.pool.ConnectionPool;
import com.stabilit.sc.pool.IPoolConnection;

public class AsyncDemoClientApplication extends ClientApplication {

	private IPoolConnection con;

	public AsyncDemoClientApplication() {
		this.con = null;
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this
				.getContext();
		
		ConnectionPool pool = ConnectionPool.getInstance();
		
		con = pool.lendConnection(applicationContext, DefaultEventListener.class);
		if (con == null) {
			throw new ServerException("no client available");
		}
		int index = 0;
		while (index++ < 10) {
			String subscribeId = null;
			// subscribe 
			if (con instanceof ISubscribe) {
				System.out.println("AsyncDemoClientApplication.run() begin subscribe");
				subscribeId = ((ISubscribe) con).subscribe();
				System.out.println("AsyncDemoClientApplication.run() subscribe done,  subscribeid = " + subscribeId);
			}
			Thread.sleep(10000);
			// unsubscribe
			if (con instanceof ISubscribe) {
				System.out.println("AsyncDemoClientApplication.run() begin unsubscribe subscribeId = " + subscribeId);
				((ISubscribe) con).unsubscribe(subscribeId);
			}
			System.out.println("AsyncDemoClientApplication.run() unsubscribe done");
			Thread.sleep(5000);
		}
		con.releaseConnection();
	}
}
