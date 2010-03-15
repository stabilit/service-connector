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
package com.stabilit.sc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.app.server.ServerConnectionFactory;
import com.stabilit.sc.server.IServerConnection;

/**
 * @author JTraber
 * 
 */
public class ServerConnectionContext implements IServerConnectionContext {

	protected String[] args;
	private String connectionType;
	private String SChost;
	private int SCport;
	private String serverHost;
	private int serverPort;
	private int poolSize;

	private Map<String, Object> attrMap;

	public ServerConnectionContext(String SChost, int SCport, String serverHost, int serverPort,
			String connectionType) {
		this.attrMap = new ConcurrentHashMap<String, Object>();
		this.SCport = SCport;
		this.SChost = SChost;
		this.connectionType = connectionType;
		this.poolSize = 3;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}

	public ServerConnectionContext(String SChost, int SCport, String connectionType) {
		this.attrMap = new ConcurrentHashMap<String, Object>();
		this.SCport = SCport;
		this.SChost = SChost;
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

	public IServerConnection create() {
		IServerConnection con = ServerConnectionFactory.newInstance(connectionType);
		con.create(this);
		return con;
	}

	@Override
	public String getSCHost() {
		return SChost;
	}

	@Override
	public int getSCPort() {
		return SCport;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getConnectionType() {
		return connectionType;
	}
}
