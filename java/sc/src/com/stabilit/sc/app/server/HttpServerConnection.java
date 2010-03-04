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
package com.stabilit.sc.app.server;

import com.stabilit.sc.exception.HttpServerConnectionException;
import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 * 
 */
public abstract class HttpServerConnection extends ServerApplication implements IHttpServerConnection {

	@Override
	public void send(SCMP scmp) throws HttpServerConnectionException {
		throw new HttpServerConnectionException("Send operation is not allowed for a HttpServerConnection according to HTTP protocol.");
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws HttpServerConnectionException {
		throw new HttpServerConnectionException("SendAndReceive operation is not allowed for a HttpServerConnection according to HTTP protocol.");
	}
}
