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
package com.stabilit.sc.client;

import org.jboss.netty.channel.ChannelFutureListener;

import com.stabilit.sc.client.factory.ClientConnectionFactory;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 * 
 */
public class Client implements IClient {

	private ClientConfig clientConfig;
	private IClientConnection clientConnection;

	@Override
	public IFactoryable newInstance() {
		return new Client();
	}

	@Override
	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
		ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
		this.clientConnection = clientConnectionFactory.newInstance(this.clientConfig.getCon());
	}

	@Override
	public void connect(String host, int port) throws ConnectionException {
	}

	@Override
	public void connect(String host, int port, ChannelFutureListener listener) throws ConnectionException {
	}

	@Override
	public void createSession() {
	}

	@Override
	public void deleteSession() {
	}

	@Override
	public void destroy() throws Exception {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		return null;
	}
}
