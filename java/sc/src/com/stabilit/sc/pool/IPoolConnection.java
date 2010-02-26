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
package com.stabilit.sc.pool;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 * 
 */
public interface IPoolConnection {

	void releaseConnection();

	boolean isAvailable();

	void send(SCMP scmp) throws Exception;

	SCMP sendAndReceive(SCMP scmp) throws Exception;

	void destroy() throws Exception;

	String getSessionId();
}
