package org.serviceconnector.ctx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.net.EncoderDecoderFactory;
import org.serviceconnector.net.FrameDecoderFactory;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.IFrameDecoder;
import org.serviceconnector.net.connection.ConnectionFactory;
import org.serviceconnector.net.res.EndpointFactory;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPMsgType;

public class AppContext {

	protected static AppContext instance;

	/** The map stores base instances by a key. */
	private static Map<String, ICommand> commands;
	private static Map<String, IEncoderDecoder> encoderDecoders;
	private static Map<String, IFrameDecoder> frameDecoders;

	// Factories
	private static CommandFactory commandFactory;
	private static final ResponderRegistry responderRegistry = new ResponderRegistry();
	private static final ConnectionFactory connectionFactory = new ConnectionFactory();
	private static final EndpointFactory endpointFactory = new EndpointFactory();
	private static final EncoderDecoderFactory encoderDecoderFactory = new EncoderDecoderFactory();
	private static final FrameDecoderFactory frameDecoderFactory = new FrameDecoderFactory();

	// Registries
	private static final SrvServiceRegistry srvServiceRegistry = new SrvServiceRegistry();
	private static final ServerRegistry serverRegistry = new ServerRegistry();
	private static final ServiceRegistry serviceRegistry = new ServiceRegistry();
	private static final SessionRegistry sessionRegistry = new SessionRegistry();
	private static final SubscriptionRegistry subscriptionRegistry = new SubscriptionRegistry();

	public AppContext() {
		AppContext.commands = new ConcurrentHashMap<String, ICommand>();
		AppContext.encoderDecoders = new ConcurrentHashMap<String, IEncoderDecoder>();
		AppContext.frameDecoders = new ConcurrentHashMap<String, IFrameDecoder>();
		AppContext.commandFactory = null;
		AppContext.encoderDecoderFactory.initEncoders(this);
		AppContext.frameDecoderFactory.initFrameDecoders(this);
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

	public Map<String, IFrameDecoder> getFrameDecoders() {
		return AppContext.frameDecoders;
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

	public ConnectionFactory getConnectionFactory() {
		return AppContext.connectionFactory;
	}

	public EncoderDecoderFactory getEncoderDecoderFactory() {
		return AppContext.encoderDecoderFactory;
	}

	public FrameDecoderFactory getFrameDecoderFactory() {
		return AppContext.frameDecoderFactory;
	}

	public EndpointFactory getEndpointFactory() {
		return AppContext.endpointFactory;
	}

	public ResponderRegistry getResponderRegistry() {
		return AppContext.responderRegistry;
	}

	public SrvServiceRegistry getSrvServiceRegistry() {
		return AppContext.srvServiceRegistry;
	}

	public ServerRegistry getServerRegistry() {
		return AppContext.serverRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return AppContext.serviceRegistry;
	}

	public SessionRegistry getSessionRegistry() {
		return AppContext.sessionRegistry;
	}

	public SubscriptionRegistry getSubscriptionRegistry() {
		return AppContext.subscriptionRegistry;
	}
}