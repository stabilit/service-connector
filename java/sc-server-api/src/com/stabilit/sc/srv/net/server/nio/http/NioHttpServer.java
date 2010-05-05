/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.srv.net.server.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ConnectionListenerSupport;
import com.stabilit.sc.common.listener.ExceptionListenerSupport;
import com.stabilit.sc.common.net.nio.NioHttpRequest;
import com.stabilit.sc.common.net.nio.NioHttpResponse;
import com.stabilit.sc.common.net.nio.NioTcpDisconnectException;
import com.stabilit.sc.common.scmp.IFaultResponse;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPErrorCode;
import com.stabilit.sc.common.scmp.SCMPFault;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMessageID;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.scmp.internal.SCMPLargeResponse;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.net.server.nio.NioCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.srv.registry.ServerRegistry.ServerRegistryItem;
import com.stabilit.sc.srv.server.ServerConnectionAdapter;

public class NioHttpServer extends ServerConnectionAdapter implements Runnable {

	private static final int THREAD_COUNT = 10;

	// The host:port combination to listen on
	private String host;
	private int port;
	private SCMPMessageID msgID;
	
	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	private final ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 10,
			TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>());

	public NioHttpServer() {
		this(null, 0);
	}

	public NioHttpServer(String host, int port) {
		this.host = host;
		this.port = port;
		this.msgID = new SCMPMessageID();
	}

	@Override
	public void create() {
		try {
			// Create a new non-blocking server socket channel
			this.serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(true);

			// Bind the server socket to the specified address and port
			InetSocketAddress isa = new InetSocketAddress(this.host, this.port);
			serverChannel.socket().bind(isa);
		} catch (IOException e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
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
				ExceptionListenerSupport.getInstance().fireException(this, e);
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
	public Thread runAsyncForTest() {
		Thread serverThread = new Thread(this);
		serverThread.start();
		return serverThread;
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
		return new NioHttpServer();
	}

	public class RequestThread implements Runnable {
		private SocketChannel socketChannel = null;
		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();

		public RequestThread(SocketChannel requestSocket) {
			this.socketChannel = requestSocket;
		}

		public void run() {
			
			SCMPLargeResponse scmpResponseComposite = null;
			
			try {
				ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
				serverRegistry.add(this.socketChannel, new ServerRegistryItem(NioHttpServer.this.server));
				serverRegistry.setThreadLocal(this.socketChannel);
				while (true) {
					NioHttpRequest request = new NioHttpRequest(socketChannel);
					NioHttpResponse response = new NioHttpResponse(socketChannel);
					NioCommandRequest commandRequest = new NioCommandRequest(request, response);
					if (scmpResponseComposite != null) {
						if (scmpResponseComposite.hasNext()) {
							commandRequest.readRequest();
							SCMP nextSCMP = scmpResponseComposite.getNext();
							response.setSCMP(nextSCMP);
							nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
							msgID.incrementPartSequenceNr();
						    response.write();
							if (scmpResponseComposite.hasNext() == false) {
								scmpResponseComposite = null;
							}
						    continue;
						}
						scmpResponseComposite = null;
					}
					ICommand command = commandRequest.readCommand();
					try {
						if (command == null) {
							SCMP scmpReq = request.getSCMP();
							SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
							scmpFault.setMessageType(scmpReq.getMessageType());
							scmpFault.setLocalDateTime();
							response.setSCMP(scmpFault);
							scmpFault.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
							msgID.incrementMsgSequenceNr();
							response.write();
							return;
						}

						ICommandValidator commandValidator = command.getCommandValidator();
						try {
							commandValidator.validate(request);
							command.run(request, response);
						} catch (Exception ex) {
							ExceptionListenerSupport.getInstance().fireException(this, ex);
							if (ex instanceof IFaultResponse) {
								((IFaultResponse) ex).setFaultResponse(response);
							}
						}
						// TODO error handling immer antworten?
					} catch (Exception ex) {
						ExceptionListenerSupport.getInstance().fireException(this, ex);
						if (NioTcpDisconnectException.class == ex.getClass()) {
							throw ex;
						}
						SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
						scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
						scmpFault.setLocalDateTime();
						response.setSCMP(scmpFault);
					}
					// check if response is large, if so create a composite for this reply
					if (response.isLarge()) {
						scmpResponseComposite = new SCMPLargeResponse(response);
						SCMP firstSCMP = scmpResponseComposite.getFirst();
						response.setSCMP(firstSCMP);
					} else {
						SCMP scmp = response.getSCMP();
						if (scmp.isPart() || request.getSCMP().isPart()) {
							msgID.incrementPartSequenceNr();
							scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
						} else {
							scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
							msgID.incrementMsgSequenceNr();
						}
					}
					response.write();
				}
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				try {
					ConnectionListenerSupport.getInstance().fireDisconnect(this);
					socketChannel.close();
				} catch (IOException ex) {
					ExceptionListenerSupport.getInstance().fireException(this, ex);
				}
			}			
		}
	}
}
