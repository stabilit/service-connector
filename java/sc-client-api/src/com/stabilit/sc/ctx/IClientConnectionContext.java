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
package com.stabilit.sc.ctx;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;


/**
 * @author JTraber
 *
 */
public interface IClientConnectionContext extends IContext {

	IClientConnection connect() throws ConnectionException;

	/**
	 * @param i
	 */
	void setPoolSize(int i);

	/**
	 * @return
	 */
	int getPort();

	/**
	 * @return
	 */
	String getHost();
}
