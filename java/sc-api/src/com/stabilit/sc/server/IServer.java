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
package com.stabilit.sc.server;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.service.ConnectionCtx;

/**
 * @author JTraber
 * 
 */
public interface IServer {

	/**
	 * @param readTimeout
	 * @param writeTimeout
	 * @throws ServiceException
	 */
	void registerServer(int readTimeout, int writeTimeout) throws ServiceException;
	
	void unregisterServer(int readTimeout, int writeTimeout) throws ServiceException;

	/**
	 * @param scmp
	 * @param timeout
	 * @param compression
	 */
	void publish(SCMP scmp, int timeout, boolean compression);

	void connect(int timeout, ConnectionCtx connectionCtx) throws ScConnectionException;
	
	void disconnect(int timeout, ConnectionCtx connectionCtx) throws ScConnectionException;

}
