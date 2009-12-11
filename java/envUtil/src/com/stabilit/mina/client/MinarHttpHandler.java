package com.stabilit.mina.client;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class MinarHttpHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		super.messageReceived(session, message);
		System.out.println(message.toString());
		System.out.println("message recieved");
	}
}
