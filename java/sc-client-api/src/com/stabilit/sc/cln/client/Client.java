/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.cln.client;

import com.stabilit.sc.cln.client.factory.ClientConnectionFactory;
import com.stabilit.sc.cln.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPComposite;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPPartID;

/**
 * @author JTraber
 * 
 */
public class Client implements IClient {

	private ClientConfigItem clientConfig;
	protected IClientConnection clientConnection;

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
		if (scmp.isPart()) {
			ret = sendAndReceiveSCMPPart((SCMPPart) scmp);
		} else {
			if (scmp.isLargeMessage()) {
				ret = sendLargeSCMPAndReceive(scmp);
			} else {
				ret = sendSmallSCMPAndReceive(scmp);
			}
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
		SCMP ret = clientConnection.sendAndReceive(scmp);
		if (ret.isPart()) {
			// request is small, response is large
			return receiveLargeResponse(scmp, (SCMPPart) ret);
		}
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
			ret = receiveLargeResponse(scmp, (SCMPPart) ret);
		}
		return ret;
	}

	private SCMP sendLargeSCMP(SCMP scmp) throws Exception {
		if (scmp.getHeader(SCMPHeaderAttributeKey.PART_ID) == null) {
			scmp.setHeader(SCMPHeaderAttributeKey.PART_ID, SCMPPartID.getNextAsString());
		}
		while (true) {
			SCMP ret = clientConnection.sendAndReceive(scmp);
			// check if request has been sent completely
			if (scmp.isRequest()) {
				// the response can be small or large, this doesn't matter,
				// we continue reading any large response later
				return ret;
			}
			if (ret.isPart() == false) {
				// this part return belongs to the request, not to the response
				return ret;
			}
		}
	}

	/**
	 * This method sends a unqiue scmp part. An scmp part is never large, but the response can be large
	 */
	private SCMP sendAndReceiveSCMPPart(SCMPPart scmpPart) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(
				scmpPart);
		clientConnection.setEncoderDecoder(encoderDecoder);
		SCMP ret = clientConnection.sendAndReceive(scmpPart);
		if (ret.isPart()) {
			// response is large
			return receiveLargeResponse(scmpPart, (SCMPPart) ret);
		}
		return ret;
	}

	private SCMP receiveLargeResponse(SCMP request, SCMPPart response) throws Exception {
		SCMPComposite scmpComposite = new SCMPComposite(request, response);
		SCMP ret = null;
		while (true) {
			ret = clientConnection.sendAndReceive(scmpComposite.getPartRequest());
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
		}
		return scmpComposite;
	}

}
