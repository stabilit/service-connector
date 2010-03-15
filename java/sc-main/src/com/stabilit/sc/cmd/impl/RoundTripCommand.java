package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.SCKernel;
import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.RoundTripMessage;
import com.stabilit.sc.service.ServiceCtx;

public class RoundTripCommand extends Command {

	@Override
	public String getKey() {
		return "roundTrip";
	}

	@Override
	public ICommand newCommand() {
		return new RoundTripCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();
		try {
			ServiceCtx serviceCtx = SCKernel.getInstance().getService(scmp.getHeader("serviceName"));
			IClientConnection conn = serviceCtx.getConn();

			if (conn == null)
				throw new CommandException("No Service registered with name: "
						+ scmp.getHeader("serviceName"));
			SCMP scmpRe = conn.sendAndReceive(scmp);

			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmpRe);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
