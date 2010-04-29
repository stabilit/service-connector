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
import com.stabilit.sc.cln.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMessageID;
import com.stabilit.sc.common.scmp.impl.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.impl.IEncoderDecoder;
import com.stabilit.sc.common.scmp.internal.SCMPComposite;
import com.stabilit.sc.common.scmp.internal.SCMPLargeRequest;

/**
 * @author JTraber
 * 
 */
public class Client implements IClient {

	private ClientConfigItem clientConfig;
	protected IClientConnection clientConnection;
	private SCMPMessageID msgID;

	public Client() {
		msgID = new SCMPMessageID();
	}

	@Override
	public IFactoryable newInstance() {
		return new Client();
	}

	@Override
	public void setClientConfig(ClientConfigItem clientConfig) {
		this.clientConfig = clientConfig;
		ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
		this.clientConnection = clientConnectionFactory.newInstance(this.clientConfig.getCon());
		clientConnection.setHost(clientConfig.getHost());
		clientConnection.setPort(clientConfig.getPort());
	}

	@Override
	public void connect() throws Exception {
		clientConnection.connect();
	}

	@Override
	public void destroy() throws Exception {
		clientConnection.destroy();
	}

	@Override
	public void disconnect() throws Exception {
		clientConnection.disconnect();
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		SCMP ret = null;
		if (scmp.isLargeMessage()) {
			ret = sendLargeSCMPAndReceive(scmp);
		} else {
			ret = sendSmallSCMPAndReceive(scmp);
		}
		return ret;
	}

	/**
	 * request is small, but response could be small or large
	 */
	private SCMP sendSmallSCMPAndReceive(SCMP scmp) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(
				scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);
		scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
		SCMP ret = clientConnection.sendAndReceive(scmp);
		if (ret.isPart()) {
			// request is small, response is large
			return receiveLargeResponse(scmp, (SCMP) ret);
		}
		msgID.incrementMsgSequenceNr();
		return ret;
	}

	/**
	 * request is large, response could be small or large
	 */

	private SCMP sendLargeSCMPAndReceive(SCMP scmp) throws Exception {
		// following code handles large messages. (chunking)
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(
				scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);

		SCMP ret = this.sendLargeSCMP(scmp); // send large request scmp
		if (ret.isPart()) {
			// request is small, response is large
			ret = receiveLargeResponse(scmp, (SCMP) ret);
		}
		msgID.incrementMsgSequenceNr();
		return ret;
	}

	private SCMP sendLargeSCMP(SCMP scmp) throws Exception {
		SCMPLargeRequest scmpLargeRequest = new SCMPLargeRequest(scmp);
		SCMP part = scmpLargeRequest.getFirst();
		msgID.incrementPartSequenceNr();
		while (part != null) {
			part.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			SCMP ret = clientConnection.sendAndReceive(part);
			// check if request has been sent completely
			if (part.isRequest()) {
				// the response can be small or large, this doesn't matter,
				// we continue reading any large response later
				return ret;
			}
			if (ret.isPart() == false) {
				// this part return belongs to the request, not to the response
				return ret;
			}
			if (scmpLargeRequest.hasNext() == false) {
				return null;
			}
			part = scmpLargeRequest.getNext();
			msgID.incrementPartSequenceNr();
		}
		return null;
	}

	private SCMP receiveLargeResponse(SCMP request, SCMP response) throws Exception {
		SCMPComposite scmpComposite = new SCMPComposite(request, response);
		SCMP ret = null;
		msgID.incrementPartSequenceNr();
		while (true) {
			SCMP scmp = scmpComposite.getPartRequest();
			scmp.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, msgID.getNextMessageID());
			ret = clientConnection.sendAndReceive(scmp);
			if (ret == null) {
				return ret;
			}
			if (ret.isFault()) {
				return ret;
			}
			scmpComposite.add(ret);
			if (ret.isPart() == false) {
				break;
			}
			msgID.incrementPartSequenceNr();
		}
		return scmpComposite;
	}

}
