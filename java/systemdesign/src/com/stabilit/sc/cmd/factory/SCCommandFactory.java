package com.stabilit.sc.cmd.factory;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.cmd.impl.FileSystemCommand;
import com.stabilit.sc.cmd.impl.SubscribeCommand;
import com.stabilit.sc.io.IRequest;

public class SCCommandFactory implements ICommandFactory {

	private Map<String, ICommand> commandMap;
	
	public SCCommandFactory() {
		commandMap = new HashMap<String, ICommand>();
		ICommand echoCommand = new EchoCommand();
		commandMap.put(echoCommand.getKey(), echoCommand);
		ICommand fileSystemCommand = new FileSystemCommand();
		commandMap.put(fileSystemCommand.getKey(), fileSystemCommand);
		ICommand subscribeCommand = new SubscribeCommand();
		commandMap.put(subscribeCommand.getKey(), subscribeCommand);
	}
	
	@Override
	public ICommand newCommand(IRequest request) {
		String key = request.getKey();
		ICommand command = commandMap.get(key);
		return command;
	}

}
