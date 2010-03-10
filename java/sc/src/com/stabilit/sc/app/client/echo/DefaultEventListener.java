package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ClientListener;
import com.stabilit.sc.pool.IPoolConnection;

public class DefaultEventListener extends ClientListener {

	//TODO ... hmm wie erzwingt man den user den default Konstruktor zu implementieren??
	
	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		super.messageReceived(conn, scmp);
		System.out.println("DemoEventListener.messageReceived()");
		Object obj = scmp.getBody();
		System.out.println(obj);
	}
}
