package com.stabilit.sc.example.server;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ClientListener;
import com.stabilit.sc.pool.IPoolConnection;

public class ServerRRListener extends ClientListener {

	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		super.messageReceived(conn, scmp);
		if(scmp.getMessageId().equals("getData")) {
			System.out.println("Getdata");
		}
	}	
}
