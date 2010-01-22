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
package com.stabilit.sc;

import com.stabilit.sc.service.ServiceFactory;

/**
 * @author JTraber
 * 
 */
public abstract class ScConnection {

	private String scHost;
	private int scPort;
	private String scProtocol;
	protected ServiceFactory serviceFactory;

	public ScConnection(String scHost, int scPort, String scProtocol, int numOfConnections) {
		super();
		this.scHost = scHost;
		this.scPort = scPort;
		this.scProtocol = scProtocol;
		this.serviceFactory = ServiceFactory.getInstance();
	}

	public String getScHost() {
		return scHost;
	}

	public int getScPort() {
		return scPort;
	}

	public String getScProtocol() {
		return scProtocol;
	}

	public void setScProtocol(String scProtocol) {
		this.scProtocol = scProtocol;
	}

	/*
	 * doing init stuff for the client - starting keep alive msg if necessary.
	 * might start up connectin pooling stuff etc.
	 */
	public abstract void attach(int timeout, int keepAliveInterval, int keepAliveTimeout);
	
	/*
	 * detach Connector from Sc
	 */
	public abstract void detach(int timeout);
}
