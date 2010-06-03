/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package com.stabilit.scm.srv.net.server.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.scm.config.IConstants;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.srv.net.server.nio.RequestThread;
import com.stabilit.scm.srv.res.EndpointAdapter;

/**
 * The Class NioHttpEndpoint. Concrete server implementation with Nio for Http.
 * 
 * @author JTraber
 */
public class NioHttpEndpoint extends EndpointAdapter implements Runnable {

	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The numberOfThreads. */
	private int numberOfThreads;
	/** The server channel. */
	private ServerSocketChannel serverChannel;
	/** The pool. */
	private ThreadPoolExecutor pool;

	/**
	 * Instantiates a new NioHttpEndpoint.
	 */
	public NioHttpEndpoint() {
		this.host = null;
		this.port = 0;
		this.numberOfThreads = IConstants.DEFAULT_NR_OF_THREADS;
		this.serverChannel = null;
		this.pool = null;
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		try {
			this.pool = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, IConstants.MAX_KEEP_ALIVE_OF_THREADS, TimeUnit.MICROSECONDS,
					new LinkedBlockingQueue<Runnable>());
			// Create a new blocking server socket channel
			this.serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(true);
			// Bind the server socket to the specified address and port
			InetSocketAddress isa = new InetSocketAddress(this.host, this.port);
			serverChannel.socket().bind(isa);
		} catch (IOException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		pool.shutdown();
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		while (true) {
			try {
				SocketChannel socketChannel;
				socketChannel = serverChannel.accept();
				pool.execute(new RequestThread(socketChannel, this.server));
			} catch (IOException e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	/** {@inheritDoc} */
	@Override
	public void runSync() throws InterruptedException {
		this.run();
	}

	/** {@inheritDoc} */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/** {@inheritDoc} */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new NioHttpEndpoint();
	}
}
