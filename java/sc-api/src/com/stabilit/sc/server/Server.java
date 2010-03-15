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

import com.stabilit.sc.context.ServerConnectionContext;

/**
 * @author JTraber
 * 
 */
public abstract class Server implements IServer {

	/** The connection context. */
	protected ServerConnectionContext connectionCtx;

	protected String serviceName;

	protected Server(String serviceName, ServerConnectionContext connectionCtx) {
		this.connectionCtx = connectionCtx;
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}
}
