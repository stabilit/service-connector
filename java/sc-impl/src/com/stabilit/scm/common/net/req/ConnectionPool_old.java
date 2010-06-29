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

import com.stabilit.scm.common.listener.IKeepAliveListener;
import com.stabilit.scm.common.listener.KeepAliveEvent;
import com.stabilit.scm.common.listener.KeepAlivePoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.RuntimePoint;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;

/**
 * @author JTraber
 */
public class ConnectionPool_old implements IConnectionPool {

	private Lock lock;
	private int port;
	private String host;
	private String conType;
	private int maxConnections;
	private int minConnections;
	private boolean closeOnFree;
	private int keepAliveInterval;
	private PoolArray freePoolArray;
	private PoolArray usedPoolArray;
	private PoolArray keepAlivePoolArray;
	private ConnectionFactory connectionFactory;
	private IKeepAliveListener keepAliveListener;

	public ConnectionPool_old(String host, int port, String conType) {
		this.host = host;
		this.port = port;
		this.conType = conType;
		this.lock = new ReentrantLock();
		this.maxConnections = 100; // TODO IConstants
		this.keepAliveInterval = 0; // TODO IConstants
		this.freePoolArray = new PoolArray();
		this.usedPoolArray = new PoolArray();
		this.keepAlivePoolArray = new PoolArray();
		this.connectionFactory = new ConnectionFactory();
		this.keepAliveListener = null;
	}

	@Override
	public IConnection getConnection() throws Exception {
		IConnection connection = null;
		try {
			lock.lock();

			connection = freePoolArray.removeFirst();
			if(connection == null) {
				// no free connection available, try to create a new one!
				return this.createNewConnection();
			}
			
			if (freePoolArray.getSize() <= 0) {
				
				return createNewConnection();
			}
			// we have a connection left
			connection = freePoolArray.removeFirst();
			usedPoolArray.add(connection);
		} finally {
			lock.unlock();
		}
		return connection;
	}

	private synchronized IConnection createNewConnection() throws Exception {
		IConnection connection;
		if (usedPoolArray.getSize() >= maxConnections) {
			// we can't create a new one - limit reached
			return null;
		}
		// we create a new one
		connection = connectionFactory.newInstance(this.conType);
		connection.setHost(this.host);
		connection.setPort(this.port);
		connection.setIdleTimeout(this.keepAliveInterval);
		connection.connect(); // can throw an exception
		this.usedPoolArray.add(connection);
		return connection;
	}

	@Override
	public void freeConnection(IConnection connection) throws Exception {
		try {
			lock.lock();
			if (this.usedPoolArray.remove(connection) == null) {
				LoggerPoint.getInstance().fireInfo(this, "connection does not exist - not possible to free");
				throw new InvalidParameterException();
			}
			if (closeOnFree) {
				// do not add the connection to free pool array - just close it immediate!
				try {
					connection.disconnect();
				} finally {
					connection.destroy();
				}
				return;
			}
			this.freePoolArray.add(connection);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public void destroy() {
		this.destroyPoolArray(this.usedPoolArray);
		this.destroyPoolArray(this.keepAlivePoolArray);
		this.destroyPoolArray(this.freePoolArray);
	}

	private void destroyPoolArray(PoolArray poolArray) {
		IConnection connection;
		for (int index = 0; index < poolArray.size; index++) {
			connection = poolArray.removeFirst();
			try {
				try {
					connection.disconnect();
				} catch (Exception e) {
					RuntimePoint.getInstance().fireRuntime(this,
							"Exception when connection pool destroys - connection destroy failed");
				} finally {
					connection.destroy();
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	@Override
	public void setCloseOnFree(boolean closeOnFree) {
		this.closeOnFree = closeOnFree;
	}

	@Override
	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

	@Override
	public void initMinConnections() {
		IConnection connection = null;
		for (int countCon = 0; countCon < minConnections; countCon++) {
			try {
				connection = this.createNewConnection();
				if (connection == null) {
					// connection null at the time maxConnections is reached - stop creating
					return;
				}
			} catch (Exception e) {
				RuntimePoint.getInstance().fireRuntime(this,
						"Exception when starting connection pool - create, connect connection failed");
				return;
			}
			this.freePoolArray.add(connection);
		}
	}

//	@Override
//	public void setKeepAliveInterval(int keepAliveInterval) {
//		this.keepAliveInterval = keepAliveInterval;
//		if (this.keepAliveInterval != 0) {
//			this.keepAliveListener = new ConnectionPoolKeepAliveListener();
//			KeepAlivePoint.getInstance().addListener(keepAliveListener);
//		}
//	}

	private class PoolArray {
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
			this.poolArray[this.size] = connection;
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

	private void keepAliveConnection(IConnection connection) throws Exception {
		try {
			lock.lock();
			if (this.freePoolArray.remove(connection) == null) {
				// this connection is no more free
				RuntimePoint.getInstance()
						.fireRuntime(this, "keep alive failed for connection, not found in free list");
				return;
			}
			this.keepAlivePoolArray.add(connection);
		} finally {
			lock.unlock();
		}
		try {
			lock.lock();
			SCMPKeepAlive keepAliveMessage = new SCMPKeepAlive();
			connection.sendAndReceive(keepAliveMessage);
			this.keepAlivePoolArray.remove(connection);
			this.freePoolArray.add(connection);
		} catch (Exception e) {
			this.keepAlivePoolArray.remove(connection);
		} finally {
			lock.unlock();
		}
	}

	private class ConnectionPoolKeepAliveListener implements IKeepAliveListener {

		@Override
		public void keepAliveEvent(KeepAliveEvent keepAliveEvent) throws Exception {
			IConnection connection = keepAliveEvent.getConnection();
			ConnectionPool_old.this.keepAliveConnection(connection);
		}
	}
}
