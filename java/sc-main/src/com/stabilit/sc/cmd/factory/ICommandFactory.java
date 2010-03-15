package com.stabilit.sc.cmd.factory;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;

public interface ICommandFactory {

	public ICommand newCommand(IRequest request);

}
