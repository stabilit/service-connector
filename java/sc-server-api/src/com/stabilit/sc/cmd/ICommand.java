package com.stabilit.sc.cmd;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMPMsgType;

public interface ICommand extends IFactoryable {

	public SCMPMsgType getKey();
	
	public String getRequestKeyName();
	
	public String getResponseKeyName();

	public ICommandValidator getCommandValidator();
	
	public void run(IRequest request, IResponse response) throws CommandException;

}
