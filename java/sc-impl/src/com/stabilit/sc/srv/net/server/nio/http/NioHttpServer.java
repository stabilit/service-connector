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
package com.stabilit.sc.srv.net.server.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.srv.net.server.nio.RequestThread;
import com.stabilit.sc.srv.server.ServerConnectionAdapter;

/**
 * The Class NioHttpServer. Concrete server connection implementation with Nio for Http.
 * 
 * @author JTraber
 */
public class NioHttpServer extends ServerConnectionAdapter implements Runnable {

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
	 * Instantiates a new NioHttpServer.
	 */
	public NioHttpServer() {
		this.host = null;
		this.port = 0;
		this.numberOfThreads = 10;
		this.serverChannel = null;
		this.pool = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#create()
	 */
	@Override
	public void create() {
		try {
			this.pool = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10, TimeUnit.MICROSECONDS,
					new LinkedBlockingQueue<Runnable>());
			// Create a new blocking server socket channel
			this.serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(true);
			// Bind the server socket to the specified address and port
			InetSocketAddress isa = new InetSocketAddress(this.host, this.port);
			serverChannel.socket().bind(isa);
		} catch (IOException e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#destroy()
	 */
	@Override
	public void destroy() {
		pool.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				SocketChannel socketChannel;
				socketChannel = serverChannel.accept();
				pool.execute(new RequestThread(socketChannel, this.server));
			} catch (IOException e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#runAsync()
	 */
	@Override
	public void runAsync() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#runSync()
	 */
	@Override
	public void runSync() throws InterruptedException {
		this.run();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setPort(int)
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IConnection#setNumberOfThreads(int)
	 */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return new NioHttpServer();
	}
}
