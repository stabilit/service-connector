package org.serviceconnector.net.res.netty;

import java.net.InetSocketAddress;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;

public abstract class NettyResponderRequestHandlerAdapter extends SimpleChannelUpstreamHandler {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(NettyResponderRequestHandlerAdapter.class);

	/**
	 * Message received.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param channel
	 *            the channel
	 */
	public void messageReceived(IRequest request, IResponse response, Channel channel) {
		// set up responderRequestHandlerTask to take care of the request
		NettyResponderRequestHandlerTask responseHandlerTask = new NettyResponderRequestHandlerTask(request, response, channel);
		AppContext.getExecutor().submit(responseHandlerTask);
	}

	/** {@inheritDoc} */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		InetSocketAddress socketAddress = (InetSocketAddress) e.getChannel().getRemoteAddress();
		if (AppContext.isScEnvironment()) {
			// if in sc environment - clean up server
			this.cleanUpDeadServer(socketAddress.getHostName(), socketAddress.getPort());
		}
	}

	/**
	 * Clean up dead server.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	private void cleanUpDeadServer(String host, int port) {
		String wildKey = "_" + host + "/" + port;
		ServerRegistry serverRegistry = AppContext.getServerRegistry();
		Set<String> keySet = serverRegistry.keySet();

		for (String key : keySet) {
			if (key.endsWith(wildKey)) {
				Server server = serverRegistry.getServer(key);
				if ((server instanceof StatefulServer) == false) {
					continue;
				}
				LOGGER.debug("clean up dead server with key " + wildKey);
				StatefulServer statefulServer = (StatefulServer) server;
				statefulServer.abortSessionsAndDestroy("clean up dead server");
			}
		}
	}
}
