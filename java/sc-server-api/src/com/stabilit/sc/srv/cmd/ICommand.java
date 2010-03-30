package com.stabilit.sc.srv.cmd;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMPMsgType;

public interface ICommand extends IFactoryable {

	public SCMPMsgType getKey();
	
	public String getRequestKeyName();
	
	public String getResponseKeyName();

	public ICommandValidator getCommandValidator();
	
	public void run(IRequest request, IResponse response) throws CommandException;

}
