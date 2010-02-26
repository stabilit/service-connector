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
package com.stabilit.sc.serviceserver;

import com.stabilit.sc.app.server.IServiceServerConnection;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.ITimeoutHandler;


/**
 * @author JTraber
 * 
 */
public interface IServiceServer {

	void start(String serviceName, Class<? extends IResponseHandler<IServiceServerConnection>> responseHandlerClass,
			Class<? extends ITimeoutHandler> timeoutHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout) throws ServiceServerException;
	
	IResponseHandler<?> getResponseHandler();
}
