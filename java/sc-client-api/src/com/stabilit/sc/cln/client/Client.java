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
import com.stabilit.sc.common.io.SCMP;

/**
 * @author JTraber
 * 
 */
public class Client implements IClient {

	private ClientConfigItem clientConfig;
	private IClientConnection clientConnection;

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
	public void connect() throws ConnectionException {
		clientConnection.connect();
	}

	@Override
	public void destroy() {
		clientConnection.destroy();
	}

	@Override
	public void disconnect() {
		clientConnection.disconnect();
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		return clientConnection.sendAndReceive(scmp);
	}
}
