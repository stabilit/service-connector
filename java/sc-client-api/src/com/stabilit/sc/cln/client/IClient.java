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

import com.stabilit.sc.cln.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.SCMP;

/**
 * @author JTraber
 * 
 */
public interface IClient extends IFactoryable {
	
	public void disconnect();

	public void destroy();

	public void connect() throws ConnectionException;

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	/**
	 * @param clientConfig
	 */
	void setClientConfig(ClientConfigItem clientConfig);
}
