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
package com.stabilit.sc.pool;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class BlockingConnPool implements IConnectionPool {

	final private SortedSet<Connection> pool = Collections.synchronizedSortedSet(new TreeSet<Connection>(
			new ConnectionComp()));
	private int tcpConnectionCount = 3;
	private IPool poolImpl;

	public BlockingConnPool(int numOfConn, IPool poolImpl) {
		this.tcpConnectionCount = numOfConn;
		this.poolImpl = poolImpl;
	}

	@Override
	public Connection borrowConnection() throws Exception {
		if (tcpConnectionCount > 0) {
			Connection conn = createConnection();
			tcpConnectionCount--;
			pool.add(conn);
			conn.setState(ConnectionState.BLOCKED);
			return conn;
		} else {
			Connection conn;

			synchronized (pool) {
				conn = pool.first();
			}
			if (!conn.getState().equals(ConnectionState.FREE))
				return null;
			conn.setState(ConnectionState.BLOCKED);
			return conn;
		}
	}

	private Connection createConnection() throws Exception {
		return poolImpl.createConnection();
	}

	public class ConnectionComp implements Comparator<Connection> {

		@Override
		public int compare(Connection o1, Connection o2) {
			return o1.getState().ordinal() < o2.getState().ordinal() ? 1 : -1;
		}
	}
}
