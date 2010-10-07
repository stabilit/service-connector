package org.serviceconnector.ctx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.net.EncoderDecoderFactory;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.scmp.SCMPMsgType;

public class AppContext {

	protected static AppContext instance;

	/** The map stores base instances by a key. */
	private static Map<String, ICommand> commands;
	private static Map<String, IEncoderDecoder> encoderDecoders;
	private static CommandFactory commandFactory;
	private static final ResponderRegistry responderRegistry = new ResponderRegistry();
	private static final ConnectionFactory connectionFactory = new ConnectionFactory();
	private static final EncoderDecoderFactory encoderDecoderFactory = new EncoderDecoderFactory();

	public AppContext() {
		AppContext.commands = new ConcurrentHashMap<String, ICommand>();
		AppContext.encoderDecoders = new ConcurrentHashMap<String, IEncoderDecoder>();
		AppContext.commandFactory = null;
		AppContext.encoderDecoderFactory.initEncoders(this);
	}

	public void initContext(CommandFactory commandFactory) {
		if (AppContext.commandFactory != null) {
			// set only one time
			return;
		}
		AppContext.commandFactory = commandFactory;
		AppContext.commandFactory.initCommands(this);
	}

	public ICommand getCommand(SCMPMsgType key) {
		return AppContext.commands.get(key.getValue());
	}

	public Map<String, ICommand> getCommands() {
		return AppContext.commands;
	}

	public Map<String, IEncoderDecoder> getEncodersDecoders() {
		return AppContext.encoderDecoders;
	}

	public CommandFactory getCommandFactory() {
		return AppContext.commandFactory;
	}

	public static AppContext getCurrentContext() {
		if (AppContext.instance == null) {
			AppContext.instance = new AppContext();
		}
		return AppContext.instance;
	}

	public ResponderRegistry getResponderRegistry() {
		return AppContext.responderRegistry;
	}

	public ConnectionFactory getConnectionFactory() {
		return AppContext.connectionFactory;
	}

	public EncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.encoderDecoderFactory;
	}
}