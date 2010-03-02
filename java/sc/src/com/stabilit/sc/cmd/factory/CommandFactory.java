package com.stabilit.sc.cmd.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.impl.AsyncCallCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.cmd.impl.GetDataCommand;
import com.stabilit.sc.cmd.impl.KeepAliveCommand;
import com.stabilit.sc.cmd.impl.RegisterCommand;
import com.stabilit.sc.cmd.impl.SubscribeCommand;
import com.stabilit.sc.cmd.impl.UnSubscribeCommand;
import com.stabilit.sc.io.IRequest;

public class CommandFactory implements ICommandFactory {

	private static ICommandFactory commandFactory = new CommandFactory();
	
	private Map<String, ICommand> commandMap;
	
	private CommandFactory() {
		commandMap = new ConcurrentHashMap<String, ICommand>();
		ICommand echoCommand = new EchoCommand();
		commandMap.put(echoCommand.getKey(), echoCommand);
		ICommand subscribeCommand = new SubscribeCommand();
		commandMap.put(subscribeCommand.getKey(), subscribeCommand);
		ICommand unSubscribeCommand = new UnSubscribeCommand();
		commandMap.put(unSubscribeCommand.getKey(), unSubscribeCommand);
		ICommand asyncCallCommand = new AsyncCallCommand(true);  // echo job creation in separate thread
		commandMap.put(asyncCallCommand.getKey(), asyncCallCommand);
		ICommand keepAliveCmd = new KeepAliveCommand();
		commandMap.put(keepAliveCmd.getKey(), keepAliveCmd);
		ICommand registerCmd = new RegisterCommand();
		commandMap.put(registerCmd.getKey(), registerCmd);
		ICommand getDataCmd = new GetDataCommand();
		commandMap.put(getDataCmd.getKey(), getDataCmd);
	}
	
	public static ICommandFactory getInstance() {
		return commandFactory;
	}
	
	@Override
	public ICommand newCommand(IRequest request) {
		String key = request.getKey();
		ICommand command = commandMap.get(key);
		return command;
	}

}
