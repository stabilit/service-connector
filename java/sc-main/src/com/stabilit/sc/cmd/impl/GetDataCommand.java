package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.SCKernel;
import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.GetDataMessage;
import com.stabilit.sc.service.ServiceCtx;

public class GetDataCommand extends Command {

	@Override
	public String getKey() {
		return "getData";
	}

	@Override
	public ICommand newCommand() {
		return new GetDataCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();

		try {
			GetDataMessage getDataMsg = (GetDataMessage) scmp.getBody();
			ServiceCtx serviceCtx = SCKernel.getInstance().getService(scmp.getHeader("serviceName"));
			IClientConnection conn = serviceCtx.getConn();
			
			if (conn == null)
				throw new CommandException("No Service registered with name: " + getDataMsg.getServiceName());
			SCMP scmpRe = conn.sendAndReceive(scmp);

			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}