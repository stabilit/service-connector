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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.IKeepAliveListener;
import com.stabilit.scm.common.listener.KeepAliveEvent;
import com.stabilit.scm.common.listener.KeepAlivePoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.RuntimePoint;

/**
 * @author JTraber
 */
public class ConnectionPool implements IConnectionPool, IFactoryable {
	private int maxConnections;
	private Lock lock;
	private PoolArray freePoolArray;
	private PoolArray usedPoolArray;
	private PoolArray keepAlivePoolArray;

	private ConnectionKey key;
	private static IKeepAliveListener keepAliveListener = new ConnectionPoolKeepAliveListener();

	static {
		KeepAlivePoint.getInstance().addListener(keepAliveListener);
	}

	public static IConnection useConnection(
			ICommunicatorConfig communicatorConfig) throws Exception {
		IConnectionPool connectionPool = ConnectionPoolFactory
				.newInstance(communicatorConfig);
		return connectionPool.getConnection(); // return an already connected
		// live instance
	}

	public static void freeConnection(IConnection connection) throws Exception {
		ConnectionKey connectionKey = (ConnectionKey) connection.getKey();
		IConnectionPool connectionPool = (IConnectionPool) ConnectionPoolFactory
				.newInstance(connectionKey);
		connectionPool.giveBackConnection(connection);
	}

	protected ConnectionPool(ICommunicatorConfig reqConfig) {
		this.key = new ConnectionKey(reqConfig.getHost(), reqConfig.getPort(),
				reqConfig.getConnectionKey());
		this.lock = new ReentrantLock();
		this.maxConnections = reqConfig.getMaxPoolSize();
		this.freePoolArray = new PoolArray();
		this.usedPoolArray = new PoolArray();
		this.keepAlivePoolArray = new PoolArray();
	}

	public void keepAliveConnection(IConnection connection) throws Exception {
		try {
			lock.lock();
			if (this.freePoolArray.remove(connection) == null) {
				// this connection is no more free
				RuntimePoint
						.getInstance()
						.fireRuntime(this,
								"keep alive failed for connection, not found in free list");
				return;
			}
			this.keepAlivePoolArray.add(connection);
			// send keep alive
		} finally {
			lock.unlock();
		}
	}

	@Override
	public IConnection getConnection() throws Exception {
		IConnection connection = getFreeConnection();
		return connection;
	}

	@Override
	public void giveBackConnection(IConnection connection) throws Exception {
		try {
			lock.lock();
			if (this.usedPoolArray.remove(connection) == null) {
				LoggerPoint.getInstance().fireInfo(this,
						"connection does not exist - not possible to free");
				throw new InvalidParameterException();
			}
			this.freePoolArray.add(connection);
		} finally {
			lock.unlock();
		}
	}

	private IConnection getFreeConnection() throws Exception {
		try {
			lock.lock();
			IConnection connection = null;
			if (freePoolArray.getSize() <= 0) {
				// no free connection available, can we create a one?
				if (usedPoolArray.getSize() >= maxConnections) {
					// no we can't
					throw new ConnectionPoolException();
				}
				// we create a new one
				ConnectionFactory connectionFactory = new ConnectionFactory();
				connection = connectionFactory.newInstance(this.key.getCon());
				connection.setHost(this.key.getHost());
				connection.setPort(this.key.getPort());
				connection.connect(); // can throw an exception
				usedPoolArray.add(connection);
				return connection;
			}
			// we have a connection left
			connection = freePoolArray.removeFirst();
			usedPoolArray.add(connection);
			return connection;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this; // a singleton
	}

	class PoolArray {
		private IConnection[] poolArray;
		private int size;

		public PoolArray() {
			this.poolArray = new IConnection[maxConnections];
			this.size = 0;
		}

		public int getSize() {
			return size;
		}

		public void add(IConnection connection) {
			this.poolArray[this.size - 1] = connection;
			this.size++;
		}

		public IConnection removeFirst() {
			if (this.size <= 0) {
				return null;
			}
			for (int i = 0; i < this.size; i++) {
				if (this.poolArray[i] != null) {
					// we found one
					IConnection connection = this.poolArray[i];
					if (i == this.size - 1) {
						this.poolArray[i] = null;
					} else {
						this.poolArray[i] = this.poolArray[this.size - 1];
						this.poolArray[this.size - 1] = null;
					}
					this.size--;
					return connection;
				}
			}
			return null;
		}

		public IConnection remove(IConnection connection) {
			if (this.size <= 0) {
				return null;
			}
			for (int i = 0; i < this.size; i++) {
				if (this.poolArray[i] == connection) {
					// we found it
					if (i == this.size - 1) {
						this.poolArray[i] = null;
					} else {
						this.poolArray[i] = this.poolArray[this.size - 1];
						this.poolArray[this.size - 1] = null;
					}
					this.size--;
					return connection;
				}
			}
			return null;
		}
	}

	private static class ConnectionPoolKeepAliveListener implements
			IKeepAliveListener {

		@Override
		public void keepAliveEvent(KeepAliveEvent keepAliveEvent)
				throws Exception {
			IConnection connection = keepAliveEvent.getConnection();
			if (connection == null) {
				RuntimePoint.getInstance().fireRuntime(this,
						"keep alive event for null connection received");
				return;
			}
			ConnectionKey connectionKey = (ConnectionKey) connection.getKey();
			ConnectionPool connectionPool = (ConnectionPool) ConnectionPoolFactory
					.newInstance(connectionKey);
			if (connectionPool == null) {
				RuntimePoint
						.getInstance()
						.fireRuntime(
								this,
								"keep alive event connection received which has no connection pool for given key = "
										+ connectionKey);
				return;
			}
			connectionPool.keepAliveConnection(connection);
		}
	}
}
