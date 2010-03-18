package com.stabilit.sc.cmd;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.msg.MsgType;

public interface ICommand extends IFactoryable {

	public MsgType getKey();
	
	public String getKeyName();

	public void run(IRequest request, IResponse response) throws CommandException;
}
