package com.stabilit.sc.example.server;

import org.apache.log4j.Logger;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ClientListener;
import com.stabilit.sc.msg.impl.GetDataMessage;
import com.stabilit.sc.msg.impl.PublishMessage;
import com.stabilit.sc.pool.IPoolConnection;

public class ServerSPListener extends ClientListener {
	
	Logger log = Logger.getLogger(ServerSPListener.class);
	int count = 0;
	
	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		super.messageReceived(conn, scmp);
	
		log.debug("Messages received " + scmp.getMessageId() + " on TcpRRServerListener");
		
		if (scmp.getMessageId().equals("publish") && count < 15) {
			log.debug("publish msg sent.");
			PublishMessage pubMsg = new PublishMessage();
			pubMsg.setAttribute("msg", "pub msg " + count);
			count++;
			scmp.setBody(pubMsg);
			conn.send(scmp);
		}
	}
}
