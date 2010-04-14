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
import com.stabilit.sc.cln.service.MessageID;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPComposite;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.impl.LargeMessageEncoderDecoder;

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
		// following code handles large messages. (chunking)
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance(scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);
		if (LargeMessageEncoderDecoder.class == encoderDecoder.getClass()) {
			if (scmp.getHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName()) == null) {
				scmp.setHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName(), MessageID.getNextAsString());
			}
			while (scmp.isPart() == false) {
				SCMP ret = clientConnection.sendAndReceive(scmp);
				if (ret.isPart() == false) {
					return ret;
				}
			}
		}
		if (scmp.isPart() == false) {
			// we are a client, not an sc
			SCMP ret = clientConnection.sendAndReceive(scmp);
			if (ret == null) {
				throw new SCMPServiceException("result scmp is null");
			}
			if (ret.isPart() == false) {
				return ret;
			}
			SCMPComposite scmpComposite = new SCMPComposite(scmp, (SCMPPart)ret);
			while (true) {
				ret = clientConnection.sendAndReceive(scmpComposite.getPartRequest());
				if (ret == null) {
					return ret;
				}
				if(ret.isFault()) {
					return ret;
				}
				scmpComposite.add(ret);
				if (ret.isPart() == false) {
					break;
				}
			}
			return scmpComposite;
		}
		return clientConnection.sendAndReceive(scmp);
	}		
}
