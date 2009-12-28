package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.MessageResult;

public class EchoCommand implements ICommand {

	@Override
	public String getKey() {
		return "echo";
	}

	@Override
	public ICommand newCommand() {
		return new EchoCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
       IMessage job = request.getJob();
       IMessageResult jobResult = new MessageResult(job);
//       System.out.println("EchoCommand.run(): job = " + job.toString());
       response.setJobResult(jobResult);
	}

}
