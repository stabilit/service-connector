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
package com.stabilit.sc.cln.client.factory;

import com.stabilit.sc.cln.client.IClientConnection;
import com.stabilit.sc.cln.net.client.netty.http.NettyHttpClientConnection;
import com.stabilit.sc.cln.net.client.netty.tcp.NettyTcpClientConnection;
import com.stabilit.sc.cln.net.client.nio.http.NioHttpClientConnection;
import com.stabilit.sc.cln.net.client.nio.tcp.NioTcpClientConnection;
import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;

public class ClientConnectionFactory extends Factory {

	public ClientConnectionFactory() {
		// jboss netty http server
		IClientConnection nettyHttpClient = new NettyHttpClientConnection();
		add("default", nettyHttpClient);
		add("netty.http", nettyHttpClient);
		// jboss netty tcp server
		IClientConnection nettyTCPClient = new NettyTcpClientConnection();
		add("netty.tcp", nettyTCPClient);
		
		IClientConnection nioTCPClient = new NioTcpClientConnection();
		add("nio.tcp", nioTCPClient); 
		
		IClientConnection nioHttpClient = new NioHttpClientConnection();
		add("nio.http", nioHttpClient); 
	}

	public IClientConnection newInstance() {
		return newInstance("default");
	}

	public IClientConnection newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IClientConnection) factoryInstance; // should be a clone if implemented
	}
}
