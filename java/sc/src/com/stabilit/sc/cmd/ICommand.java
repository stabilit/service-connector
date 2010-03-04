package com.stabilit.sc.cmd;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;

public interface ICommand {

	public String getKey();

	public ICommand newCommand();

	public void run(IRequest request, IResponse response, IConnection conn) throws CommandException;

	public void run(IRequest request, IResponse response) throws CommandException;
}
