package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.msg.impl.RegisterMessage;

public class RegisterServiceCommand extends CommandAdapter  {

	public RegisterServiceCommand() {
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();

		ISession session = request.getSession(true);
		RegisterMessage registerMessage = (RegisterMessage) scmp.getBody();

//		IClientConnection clientConn = ClientConnectionFactory.newInstance("netty.tcp");
//		try {
//			// Listener is necessary because awaitUniterruptly in client is not allowed inside I/O thread. Be
//			// carefull when using this listener. High potential of dead lock!
//			TCPClientConnectListener listener = new TCPClientConnectListener();
//			// TODO Check if ok
//			clientConn.connect((String) registerMessage.getAttribute("host"), Integer
//					.valueOf((String) registerMessage.getAttribute("port")), listener);
//
//			// TODO Achtung netty abhängigkeit .. sollte noch ausgelagert werden!
//			ChannelFuture future = listener.getOperationCompleteEventSync();
//			clientConn.setChannel(future.getChannel());
//		} catch (ConnectionException e1) {
//			e1.printStackTrace();
//		}
//
//		SCKernel.getInstance().registerService(scmp.getHeader().get("serviceName"), clientConn);
//
//		try {
//			// TODO registerMessage response generieren und zurücksenden!
//			SCMP scmpRes = new SCMP(registerMessage);
//			response.setSession(session);
//			response.setSCMP(scmpRes);
//
//		} catch (Exception e) {
//			throw new CommandException(e.toString());
//		}
	}
	
	@Override
	public IFactoryable newInstance() {
		return this;
	}
}
