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
package com.stabilit.sc.app.client;

import java.net.URL;

import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * @author JTraber
 *
 */
public interface IClientConnection {
	
	public boolean isAvailable();
	
	public void setAvailable(boolean available);
	
	public String getSessionId();
	
	public void setEndpoint(URL url);

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	public void send(SCMP scmp) throws Exception;

	public void disconnect() throws Exception;
	
	public void destroy() throws Exception;

	public void connect(Class<? extends IClientListener> scListener) throws ConnectionException;

	public void deleteSession();

	public void createSession();
	
	public void setDecorator(IPoolConnection dec);
}
