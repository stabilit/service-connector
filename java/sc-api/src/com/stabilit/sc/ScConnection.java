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
package com.stabilit.sc;

import com.stabilit.sc.exception.ScConnectionException;

/**
 * The Class ScConnection, represents a connection between a connector and a Sc.
 * 
 * @author JTraber
 */
public abstract class ScConnection {

	/** The sc host. */
	private String scHost;

	/** The sc port. */
	private int scPort;

	/** Used protocol for transportation. */
	private MessageTransportType scProtocol;

	/**
	 * Instantiates a new sc connection.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param scProtocol
	 *            used protocol
	 * @param numOfConnections
	 *            the number of connections used by Sc
	 */
	public ScConnection(String scHost, int scPort, MessageTransportType scProtocol, int numOfConnections) {
		super();
		this.scHost = scHost;
		this.scPort = scPort;
		this.scProtocol = scProtocol;
	}

	/**
	 * Gets the sc host.
	 * 
	 * @return the sc host
	 */
	public String getScHost() {
		return scHost;
	}

	/**
	 * Gets the sc port.
	 * 
	 * @return the sc port
	 */
	public int getScPort() {
		return scPort;
	}

	/**
	 * Gets used protocol.
	 * 
	 * @return used protocol
	 */
	public MessageTransportType getScProtocol() {
		return scProtocol;
	}

	/**
	 * Sets used protocol.
	 * 
	 * @param scProtocol
	 *            protocol to use
	 */
	public void setScProtocol(MessageTransportType scProtocol) {
		this.scProtocol = scProtocol;
	}

	/**
	 * Attach a connector to a Sc.
	 * 
	 * @param timeout
	 *            the timeout
	 * @param keepAliveInterval
	 *            the keep alive interval
	 * @param keepAliveTimeout
	 *            the keep alive timeout
	 * 
	 * @throws ScConnectionException
	 *             exception in attach process
	 */
	public abstract void attach(int timeout, int keepAliveInterval, int keepAliveTimeout)
			throws ScConnectionException;

	/**
	 * Detach connector from Sc.
	 * 
	 * @param timeout the timeout
	 * 
	 * @throws ScConnectionException exception in detach process
	 */
	public abstract void detach(int timeout) throws ScConnectionException;
} 
