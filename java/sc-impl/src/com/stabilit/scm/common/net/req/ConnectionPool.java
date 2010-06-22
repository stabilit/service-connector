/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.net.req;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.LoggerPoint;

/**
 * @author JTraber
 */
public class ConnectionPool implements IConnectionPool, IFactoryable {

	private int maxConnections;
	private List<IConnection> freePoolItemList;
	private List<IConnection> usedPoolItemList; // TODO change to a better collection - remove is slow and happens very often
	private ConnectionKey key;

	public static IConnection useConnection(ICommunicatorConfig communicatorConfig) throws Exception {
		IConnectionPool connectionPool = ConnectionPoolFactory.newInstance(communicatorConfig);
		return connectionPool.getConnection(); // return an already connected live instance
	}

	public static void freeConnection(IConnection connection) throws Exception {
		ConnectionKey connectionKey = (ConnectionKey) connection.getKey();
		IConnectionPool connectionPool = (IConnectionPool) ConnectionPoolFactory.newInstance(connectionKey);
		connectionPool.giveBackConnection(connection);
	}

	protected ConnectionPool(ICommunicatorConfig reqConfig) {
		this.key = new ConnectionKey(reqConfig.getHost(), reqConfig.getPort(), reqConfig.getConnectionKey());
		this.maxConnections = reqConfig.getMaxPoolSize();
		this.freePoolItemList = Collections.synchronizedList(new ArrayList<IConnection>());
		this.usedPoolItemList = Collections.synchronizedList(new ArrayList<IConnection>());	
	}

	@Override
	public IConnection getConnection() throws Exception {
		IConnection connection = getFreeConnection();
		return connection;
	}

	@Override
	public void giveBackConnection(IConnection connection) throws Exception {
		if (this.usedPoolItemList.remove(connection) == false) {
			LoggerPoint.getInstance().fireInfo(this, "connection does not exist - not possible to free");
			throw new InvalidParameterException();
		}
		connection.setKeepAlive(true);
		this.freePoolItemList.add(connection);
	}

	private synchronized IConnection getFreeConnection() throws Exception {
		IConnection connection = null;
		if (freePoolItemList.size() <= 0) {			
			// no free connection available, can we create a one?
			if (usedPoolItemList.size() >= maxConnections) {
				// no we can't
				throw new ConnectionPoolException();			
			}
			// we create a new one
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connection = connectionFactory.newInstance(this.key.getCon());
			connection.setHost(this.key.getHost());
			connection.setPort(this.key.getPort());
			connection.connect();  // can throw an exception
			connection.setKeepAlive(false);
			usedPoolItemList.add(connection);
			return connection;
		} 
		// we have a connection left
	    connection = freePoolItemList.remove(0);
		connection.setKeepAlive(false);
		usedPoolItemList.add(connection);
		return connection;
	}

	@Override
	public IFactoryable newInstance() {
		return this; // a singleton
	}
}
