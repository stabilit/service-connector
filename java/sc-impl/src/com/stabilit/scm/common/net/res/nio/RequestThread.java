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
package com.stabilit.scm.common.net.res.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.stabilit.scm.listener.ConnectionPoint;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.LoggerPoint;
import com.stabilit.scm.listener.PerformancePoint;
import com.stabilit.scm.sc.registry.ResponderRegistry;
import com.stabilit.scm.sc.registry.ResponderRegistry.ResponderRegistryItem;
import com.stabilit.scm.scmp.IFaultResponse;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPFault;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMessageID;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.scmp.internal.SCMPCompositeSender;
import com.stabilit.scm.srv.cmd.ICommand;
import com.stabilit.scm.srv.cmd.ICommandValidator;
import com.stabilit.scm.srv.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.res.IResponder;

/**
 * The Class RequestThread. Class is responsible for an incoming request. Knows process of validating/running a command
 * and deals with large messages.
 */
public class RequestThread implements Runnable {

	/** The socket channel. */
	private SocketChannel socketChannel = null;
	/** The command factory. */
	CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
	/** The server. */
	private IResponder server = null;
	/** The msg id. */
	private SCMPMessageID msgID;

	/**
	 * Instantiates a new RequestThread.
	 * 
	 * @param requestSocket
	 *            the request socket
	 * @param server
	 *            the server
	 */
	public RequestThread(SocketChannel requestSocket, IResponder server) {
		this.socketChannel = requestSocket;
		this.server = server;
		this.msgID = new SCMPMessageID();
	}

	/** {@inheritDoc} */
	public void run() {

		SCMPCompositeSender scmpLargeResponse = null;
		try {
			ResponderRegistry serverRegistry = ResponderRegistry.getCurrentInstance();
			// adds server to registry
			serverRegistry.add(this.socketChannel, new ResponderRegistryItem(this.server));
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
						SCMPFault scmpFault = new SCMPFault(SCMPError.REQUEST_UNKNOWN);
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
						if (LoggerPoint.getInstance().isDebug()) {
							LoggerPoint.getInstance().fireDebug(this, "Run command [" + command.getKey() + "]");
						}
						PerformancePoint.getInstance().fireBegin(command, "run");
						command.run(request, response);
						PerformancePoint.getInstance().fireEnd(command, "run");
					} catch (Exception ex) {
						ExceptionPoint.getInstance().fireException(this, ex);
						if (ex instanceof IFaultResponse) {
							((IFaultResponse) ex).setFaultResponse(response);
						}
					}
				} catch (Exception ex) {
					ExceptionPoint.getInstance().fireException(this, ex);
					if (NioTcpDisconnectException.class == ex.getClass()) {
						// no answer need to be sent when client is disconnected
						throw ex;
					}
					SCMPFault scmpFault = new SCMPFault(SCMPError.SERVER_ERROR);
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
			ExceptionPoint.getInstance().fireException(this, e);
			try {
				ConnectionPoint.getInstance().fireDisconnect(this, this.socketChannel.socket().getLocalPort());
				socketChannel.close();
			} catch (IOException ex) {
				ExceptionPoint.getInstance().fireException(this, ex);
			}
		}
	}
}
