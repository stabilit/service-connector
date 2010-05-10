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
package com.stabilit.sc.cln.client;

import com.stabilit.sc.cln.client.factory.ClientConnectionFactory;
import com.stabilit.sc.cln.config.IClientConfigItem;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.net.EncoderDecoderFactory;
import com.stabilit.sc.net.IEncoderDecoder;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessageID;
import com.stabilit.sc.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.sc.scmp.internal.SCMPCompositeSender;

/**
 * The Class Client. Implements a general behavior of a client. Defines how to connect/disconnect, send/receive has
 * to process. Handling of large request/response is defined on this level.
 * 
 * @author JTraber
 */
public class Client implements IClient {

	/** The client config. */
	private IClientConfigItem clientConfig;
	/** The client connection. */
	protected IClientConnection clientConnection;
	/** The msg id for the next request. */
	private SCMPMessageID msgID;

	/**
	 * Instantiates a new client.
	 */
	public Client() {
		msgID = new SCMPMessageID();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return new Client();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#setClientConfig(com.stabilit.sc.cln.config.IClientConfigItem)
	 */
	@Override
	public void setClientConfig(IClientConfigItem clientConfig) {
		this.clientConfig = clientConfig;
		ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
		this.clientConnection = clientConnectionFactory.newInstance(this.clientConfig.getCon());
		clientConnection.setHost(clientConfig.getHost());
		clientConnection.setPort(clientConfig.getPort());
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#connect()
	 */
	@Override
	public void connect() throws Exception {
		clientConnection.connect();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		clientConnection.destroy();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#disconnect()
	 */
	@Override
	public void disconnect() throws Exception {
		clientConnection.disconnect();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#sendAndReceive(com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		SCMP ret = null;
		// differ if scmp is large or not, sending procedure is different
		if (scmp.isLargeMessage()) {
			ret = sendLargeSCMPAndReceive(scmp);
		} else {
			ret = sendSmallSCMPAndReceive(scmp);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClient#toHashCodeString()
	 */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	/**
	 * request is small but response could be small or large.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the SCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMP sendSmallSCMPAndReceive(SCMP scmp) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);
		if (scmp.isGroup()) {
			msgID.incrementPartSequenceNr();
		}
		scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		// process send and receive
		SCMP ret = clientConnection.sendAndReceive(scmp);

		if (scmp.isPart()) {
			// incoming scmp is a part groupCall is made by client - part response can be ignored
			return ret;
		}

		if (ret.isPart()) {
			// response is a part - response is large, continue pulling
			return receiveLargeResponse(scmp, (SCMP) ret);
		}
		msgID.incrementMsgSequenceNr();
		return ret;
	}

	/**
	 * request is large, response could be small or large.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the SCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMP sendLargeSCMPAndReceive(SCMP scmp) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);

		SCMP ret = this.sendLargeSCMP(scmp); // send large request scmp

		if (ret.isPart() && scmp.isGroup() == false) {
			// response is a part - response is large, continue pulling
			ret = receiveLargeResponse(scmp, (SCMP) ret);
		}
		if (scmp.isGroup() == false) {
			msgID.incrementMsgSequenceNr();
		}
		return ret;
	}

	/**
	 * Sends large scmp.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMP sendLargeSCMP(SCMP scmp) throws Exception {
		// SCMPLargeRequest handles splitting, works like an iterator
		SCMPCompositeSender scmpLargeRequest = new SCMPCompositeSender(scmp);
		SCMP part = scmpLargeRequest.getFirst();
		msgID.incrementPartSequenceNr();
		while (part != null) {
			part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			SCMP ret = clientConnection.sendAndReceive(part);

			if (part.isRequest()) {
				/*
				 * request has been sent completely. The response can be small or large, this doesn't matter, we
				 * continue reading any large response later
				 */
				return ret;
			}
			if (scmpLargeRequest.hasNext() == false) {
				if (scmp.isGroup()) {
					/*
					 * client processes group call, he needs to get the response - happens in special case: client
					 * sends a single part of a group but content is to large and we need to split
					 */
					return ret;
				}
				WarningListenerSupport.getInstance().fireWarning(this,
						"sendLargeRequest.hasNext() == false but part request not done");
				return null;
			}
			part = scmpLargeRequest.getNext();
			msgID.incrementPartSequenceNr();
		}
		return null;
	}

	/**
	 * Receive large response.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	private SCMP receiveLargeResponse(SCMP request, SCMP response) throws Exception {
		// SCMPComposite handles parts of large requests, putting all together
		SCMPCompositeReceiver scmpComposite = new SCMPCompositeReceiver(request, response);
		SCMP ret = null;
		msgID.incrementPartSequenceNr();
		while (true) {
			SCMP scmp = scmpComposite.getPartRequest();
			scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			ret = clientConnection.sendAndReceive(scmp); // pull

			if (ret == null) {
				return ret;
			}

			if (ret.isFault()) {
				// response is fault stop receiving
				return ret;
			}
			scmpComposite.add(ret);
			if (ret.isPart() == false) {
				// response received
				break;
			}
			msgID.incrementPartSequenceNr();
		}
		return scmpComposite;
	}
}
