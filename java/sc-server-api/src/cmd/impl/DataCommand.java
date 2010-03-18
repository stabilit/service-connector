package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.MsgType;
import com.stabilit.sc.msg.impl.GetDataMessage;

public class DataCommand implements ICommand {

	@Override
	public MsgType getKey() {
		return MsgType.DATA;
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();

		try {
			GetDataMessage getDataMsg = (GetDataMessage) scmp.getBody();
//			ServiceCtx serviceCtx = SCKernel.getInstance().getService(scmp.getHeader("serviceName"));
//			IClientConnection conn = serviceCtx.getConn();
			
//			if (conn == null)
//				throw new CommandException("No Service registered with name: " + getDataMsg.getServiceName());
//			SCMP scmpRe = conn.sendAndReceive(scmp);

			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}
	
	@Override
	public String getKeyName() {
		return this.getKey().getName();
	}
}