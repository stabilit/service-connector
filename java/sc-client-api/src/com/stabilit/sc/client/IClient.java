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

import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 * 
 */
public interface IClient extends IFactoryable {

	public void deleteSession();

	public void createSession();

	public void disconnect();

	public void destroy() throws Exception;

	public void connect(String host, int port) throws ConnectionException;

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	void connect(String host, int port, ChannelFutureListener listener) throws ConnectionException;

	/**
	 * @param clientConfig
	 */
	void setClientConfig(ClientConfig clientConfig);
}
