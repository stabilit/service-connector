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
package com.stabilit.sc.service;

import com.stabilit.sc.app.client.IConnection;

/**
 * The Class ServiceCtx represents the context of a service.
 * 
 * @author JTraber
 */
public class ServiceCtx {

	/** The service name. */
	private String serviceName;
	private IConnection conn = null;

	private int subPubIndex = 0;

	/**
	 * Instantiates a new service context.
	 * 
	 * @param serviceName
	 *            the service name
	 */
	public ServiceCtx(String serviceName, IConnection conn) {
		super();
		this.serviceName = serviceName;
		this.conn = conn;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public IConnection getConn() {
		return conn;
	}

	public void setConn(IConnection conn) {
		this.conn = conn;
	}

	public int getSubPubIndex() {
		return subPubIndex;
	}

	public void setSubPubIndex(int subPubIndex) {
		this.subPubIndex = subPubIndex;
	}
}
