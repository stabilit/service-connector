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
package com.stabilit.sc.srv.server.factory;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.net.server.netty.http.NettyHttpServerConnection;
import com.stabilit.sc.srv.net.server.netty.tcp.NettyTcpServerConnection;
import com.stabilit.sc.srv.net.server.nio.http.NioHttpServer;
import com.stabilit.sc.srv.net.server.nio.tcp.NioTcpServer;
import com.stabilit.sc.srv.server.IServerConnection;

public class ServerConnectionFactory extends Factory {

	public ServerConnectionFactory() {
		// jboss netty http server
		IServerConnection nettyHttpServer = new NettyHttpServerConnection();
		add("default", nettyHttpServer);
		add("netty.http", nettyHttpServer);
		// jboss netty tcp server
		IServerConnection nettyTCPServer = new NettyTcpServerConnection();
		add("netty.tcp", nettyTCPServer);
		
		// nioServer
		IServerConnection nioTcpServer = new NioTcpServer();
		add("nio.tcp", nioTcpServer);
		
		// nioServer
		IServerConnection nioHttpServer = new NioHttpServer();
		add("nio.http", nioHttpServer);
	}

	public IServerConnection newInstance() {
		return newInstance("default");
	}

	public IServerConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IServerConnection) factoryInstance; // should be a clone if implemented
	}

}
