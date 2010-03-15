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
	private String srcHost;
	private int srcPort;
	private String tarHost;
	private int tarPort;
	private int poolSize;

	private Map<String, Object> attrMap;

	public ServerConnectionContext(String srcHost, int srcPort, String tarHost, int tarPort,
			String connectionType) {
		this.attrMap = new ConcurrentHashMap<String, Object>();
		this.srcPort = srcPort;
		this.srcHost = srcHost;
		this.connectionType = connectionType;
		this.poolSize = 3;
		this.tarHost = tarHost;
		this.tarPort = tarPort;
	}

	public ServerConnectionContext(String srcHost, int srcPort, String connectionType) {
		this.attrMap = new ConcurrentHashMap<String, Object>();
		this.srcHost = srcHost;
		this.srcPort = srcPort;
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
	public String getSrcHost() {
		return srcHost;
	}

	@Override
	public int getSrcPort() {
		return srcPort;
	}

	public String getTarHost() {
		return tarHost;
	}

	public int getTarPort() {
		return tarPort;
	}

	public String getConnectionType() {
		return connectionType;
	}
}
