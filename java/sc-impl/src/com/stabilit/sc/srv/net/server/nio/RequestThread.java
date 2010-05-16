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
package com.stabilit.sc.srv.net.server.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.stabilit.sc.listener.ConnectionListenerSupport;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.listener.PerformanceListenerSupport;
import com.stabilit.sc.net.nio.NioHttpRequest;
import com.stabilit.sc.net.nio.NioHttpResponse;
import com.stabilit.sc.net.nio.NioTcpDisconnectException;
import com.stabilit.sc.scmp.IFaultResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessageID;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.internal.SCMPCompositeSender;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.net.server.nio.NioCommandRequest;
import com.stabilit.sc.srv.registry.ServerRegistry;
import com.stabilit.sc.srv.registry.ServerRegistry.ServerRegistryItem;
import com.stabilit.sc.srv.server.IServer;

/**
 * The Class RequestThread. Class is responsible for an incoming request. Knows process of validating/running a
 * command and deals with large messages.
 */
public class RequestThread implements Runnable {

	/** The socket channel. */
	private SocketChannel socketChannel = null;
	/** The command factory. */
	CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
	/** The server. */
	private IServer server = null;
	/** The msg id. */
	private SCMPMessageID msgID;

	/**
	 * Instantiates a new RequestThread.
	 * 
	 * @param requestSocket
	 *            the request socket
	 */
	public RequestThread(SocketChannel requestSocket, IServer server) {
		this.socketChannel = requestSocket;
		this.server = server;
		this.msgID = new SCMPMessageID();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		SCMPCompositeSender scmpLargeResponse = null;
		try {
			ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
			// adds server to registry
			serverRegistry.add(this.socketChannel, new ServerRegistryItem(this.server));
			// needs to set a key in thread local to identify thread later and get access to the server
			serverRegistry.setThreadLocal(this.socketChannel);
			while (true) {
				NioHttpRequest request = new NioHttpRequest(socketChannel);
				NioHttpResponse response = new NioHttpResponse(socketChannel);
				NioCommandRequest commandRequest = new NioCommandRequest(request, response);
				if (scmpLargeResponse != null) {
					// sending of a large response has already been started and incoming scmp is a pull request
					if (scmpLargeResponse.hasNext()) {
						// there are still parts to send to complete request
						commandRequest.readRequest();
						SCMPMessage nextSCMP = scmpLargeResponse.getNext();
						response.setSCMP(nextSCMP);
						nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
						msgID.incrementPartSequenceNr();
						response.write();
						if (scmpLargeResponse.hasNext() == false) {
							scmpLargeResponse = null;
						}
						// pull request has been sent continue reading
						continue;
					}
					scmpLargeResponse = null;
				}
				ICommand command = commandRequest.readCommand();
				try {
					if (command == null) {
						SCMPMessage scmpReq = request.getMessage();
						SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.REQUEST_UNKNOWN);
						scmpFault.setMessageType(scmpReq.getMessageType());
						scmpFault.setLocalDateTime();
						response.setSCMP(scmpFault);
						scmpFault.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
						msgID.incrementMsgSequenceNr();
						response.write();
						return;
					}
					// validate request and run command
					ICommandValidator commandValidator = command.getCommandValidator();
					try {
						commandValidator.validate(request);
						if (LoggerListenerSupport.getInstance().isDebug()) {
							LoggerListenerSupport.getInstance().fireDebug(this,
									"Run command [" + command.getKey() + "]");
						}
						if (PerformanceListenerSupport.getInstance().isOn()) {
							PerformanceListenerSupport.getInstance().fireBegin(this, System.currentTimeMillis());
							command.run(request, response);
							PerformanceListenerSupport.getInstance().fireEnd(this, System.currentTimeMillis());
						} else {
							command.run(request, response);
						}
					} catch (Exception ex) {
						ExceptionListenerSupport.getInstance().fireException(this, ex);
						if (ex instanceof IFaultResponse) {
							((IFaultResponse) ex).setFaultResponse(response);
						}
					}
				} catch (Exception ex) {
					ExceptionListenerSupport.getInstance().fireException(this, ex);
					if (NioTcpDisconnectException.class == ex.getClass()) {
						// no answer need to be sent when client is disconnected
						throw ex;
					}
					SCMPFault scmpFault = new SCMPFault(SCMPErrorCode.SERVER_ERROR);
					scmpFault.setMessageType(SCMPMsgType.UNDEFINED.getResponseName());
					scmpFault.setLocalDateTime();
					response.setSCMP(scmpFault);
				}

				if (response.isLarge()) {
					// response is large, create a large response for reply
					scmpLargeResponse = new SCMPCompositeSender(response.getSCMP());
					SCMPMessage firstSCMP = scmpLargeResponse.getFirst();
					response.setSCMP(firstSCMP);
				} else {
					SCMPMessage message = response.getSCMP();
					if (message.isPart() || request.getMessage().isPart()) {
						msgID.incrementPartSequenceNr();
						message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
					} else {
						message.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
						msgID.incrementMsgSequenceNr();
					}
				}
				response.write();
				// needed for testing
				if ("true".equals(response.getSCMP().getHeader("kill"))) {
					this.server.destroy();
					return;

				}
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
