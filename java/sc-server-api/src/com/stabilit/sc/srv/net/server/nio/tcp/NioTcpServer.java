package com.stabilit.sc.srv.net.server.nio.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IFaultResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.net.nio.NioTcpDisconnectException;
import com.stabilit.sc.common.net.nio.NioTcpRequest;
import com.stabilit.sc.common.net.nio.NioTcpResponse;
import com.stabilit.sc.srv.cmd.CommandRequest;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.srv.registry.ServerRegistry.ServerRegistryItem;
import com.stabilit.sc.srv.server.ServerConnectionAdapter;

public class NioTcpServer extends ServerConnectionAdapter implements Runnable {

	private static final int THREAD_COUNT = 10;

	// The host:port combination to listen on
	private String host;
	private int port;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	private final ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 10,
			TimeUnit.MICROSECONDS, new LinkedBlockingQueue());

	public NioTcpServer() {
	}

	public NioTcpServer(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}

	@Override
	public void create() {
		try {
			// Create a new selector
			this.selector = SelectorProvider.provider().openSelector();

			// Create a new non-blocking server socket channel
			this.serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(true);

			// Bind the server socket to the specified address and port
			InetSocketAddress isa = new InetSocketAddress(this.host, this.port);
			serverChannel.socket().bind(isa);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		pool.shutdown();
	}

	@Override
	public void run() {
		while (true) {
			try {
				SocketChannel socketChannel;
				socketChannel = serverChannel.accept();
				pool.execute(new RequestThread(socketChannel));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	@Override
	public void runSync() throws InterruptedException {
		this.run();
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public IFactoryable newInstance() {
		return new NioTcpServer();
	}

	public class RequestThread implements Runnable {
		private SocketChannel socketChannel = null;
		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();

		public RequestThread(SocketChannel requestSocket) {
			this.socketChannel = requestSocket;
		}

		public void run() {
			try {
				ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
				serverRegistry.add(this.socketChannel, new ServerRegistryItem(NioTcpServer.this.server));
				serverRegistry.setThreadLocal(this.socketChannel);
				while (true) {
					NioTcpRequest request = new NioTcpRequest(socketChannel);
					NioTcpResponse response = new NioTcpResponse(socketChannel);
					CommandRequest commandRequest = new CommandRequest(request, response);
					ICommand command = commandRequest.readCommand();
					try {
						if (command == null) {
							SCMP scmpReq = request.getSCMP();                    				
							SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
							scmpFault.setMessageType(scmpReq.getMessageType());
							scmpFault.setLocalDateTime();
							response.setSCMP(scmpFault);
							response.write();
							return;
						}
						
						ICommandValidator commandValidator = command.getCommandValidator();
						try {
							commandValidator.validate(request, response);
							command.run(request, response);
						} catch (Exception ex) {
							if (ex instanceof IFaultResponse) {
								((IFaultResponse) ex).setFaultResponse(response);
							}
						}
						// TODO error handling immer antworten?
					} catch (Exception ex) {
						if (NioTcpDisconnectException.class == ex.getClass()) {
							throw ex;
						}
						SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
						scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
						scmpFault.setLocalDateTime();
						response.setSCMP(scmpFault);
					}
					response.write();
				}

			} catch (Throwable e) {
				try {
					socketChannel.close();
				} catch (IOException ex) {
				}
			}
		}
	}
}
