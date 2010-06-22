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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;

/**
 * @author JTraber
 */
public class ConnectionPoolFactory extends Factory {

	private static ConnectionPoolFactory connectionPoolFactory = new ConnectionPoolFactory();
	private static Lock lock = new ReentrantLock();

	public ConnectionPoolFactory() {
	}

	public static IConnectionPool newInstance(ICommunicatorConfig communicatorConfig) {
		try {
			lock.lock();
			ConnectionKey connectionPoolKey = new ConnectionKey(communicatorConfig);
			ConnectionPool connectionPool = (ConnectionPool) connectionPoolFactory.getInstance(connectionPoolKey);
			if (connectionPool != null) {
				return connectionPool;
			}
			connectionPool = new ConnectionPool(communicatorConfig);
			// we have a connection pool now, but we are not sure if the server endpoint does exist or not
			connectionPoolFactory.add(connectionPoolKey, connectionPool);
			if (LoggerPoint.getInstance().isInfo()) {
				LoggerPoint.getInstance().fireInfo(connectionPoolFactory,
						"create new connetion pool for key " + connectionPoolKey);
			}
			return connectionPool;
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(connectionPoolFactory, e);
		} finally {
			lock.unlock();
		}
		return null;
	}
	
	public static IConnectionPool newInstance(ConnectionKey connectionKey) {
		try {
			lock.lock();
			ConnectionPool connectionPool = (ConnectionPool) connectionPoolFactory.getInstance(connectionKey);
			if (connectionPool != null) {
				return connectionPool;
			}
			return connectionPool;
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(connectionPoolFactory, e);
		} finally {
			lock.unlock();
		}
		return null;
	}

}
