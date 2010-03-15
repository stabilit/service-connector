package com.stabilit.sc.cmd.impl;

import org.jboss.netty.channel.ChannelFuture;

import com.stabilit.sc.SCKernel;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.client.IConnection;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.RegisterMessage;
import com.stabilit.sc.net.client.netty.tcp.TCPClientConnectListener;

public class RegisterCommand extends ExtendedCommand {

	public RegisterCommand() {
	}

	@Override
	public String getKey() {
		return "register";
	}

	@Override
	public ICommand newCommand() {
		return new RegisterCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		throw new CommandException("Unsupported operation for " + RegisterCommand.class + " Command.");
	}

	@Override
	public void run(IRequest request, IResponse response, IConnection conn) throws CommandException {
		SCMP scmp = request.getSCMP();

		ISession session = request.getSession(true);
		RegisterMessage registerMessage = (RegisterMessage) scmp.getBody();

		IClientConnection clientConn = ClientConnectionFactory.newInstance("netty.tcp");
		try {
			// Listener is necessary because awaitUniterruptly in client is not allowed inside I/O thread. Be
			// carefull when using this listener. High potential of dead lock!
			TCPClientConnectListener listener = new TCPClientConnectListener();
			// TODO Check if ok
			clientConn.connect((String) registerMessage.getAttribute("host"), Integer
					.valueOf((String) registerMessage.getAttribute("port")), listener);

			// TODO Achtung netty abhängigkeit .. sollte noch ausgelagert werden!
			ChannelFuture future = listener.getOperationCompleteEventSync();
			clientConn.setChannel(future.getChannel());
		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}

		SCKernel.getInstance().registerService(scmp.getHeader().get("serviceName"), clientConn);

		try {
			// TODO registerMessage response generieren und zurücksenden!
			SCMP scmpRes = new SCMP(registerMessage);
			response.setSession(session);
			response.setSCMP(scmpRes);

		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
