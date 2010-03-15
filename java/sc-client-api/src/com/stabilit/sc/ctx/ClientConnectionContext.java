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

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.exception.ConnectionException;

/**
 * @author JTraber
 * 
 */
public class ClientConnectionContext implements IClientConnectionContext {

	protected String[] args;
	private String connectionType;
	private String host;
	private int port;
	private int poolSize;

	private Map<String, Object> attrMap;

	public ClientConnectionContext(String host, int port, String connectionType) throws MalformedURLException {
		this.attrMap = new ConcurrentHashMap<String, Object>();
		this.port = port;
		this.host = host;
		this.connectionType = connectionType;
		this.poolSize = 3;
	}

	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	@Override
	public IClientConnection connect() throws ConnectionException {
		IClientConnection con = ClientConnectionFactory.newInstance(connectionType);
		con.connect(host, port);
		return con;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public int getPort() {
		return port;
	}

	public String getConnectionType() {
		return connectionType;
	}
}
