/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.mina.message.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;

public class EasyMessageClient {
	private static final long serialVersionUID = 1538675161745436968L;
	private EasyMessageHandler handler;
	private SocketConnector connector;
	private IoSession session;

	public static void main(String[] args) {
		EasyMessageClient client = new EasyMessageClient();
		client.run();
	}

	private void run() {
		connector = new SocketConnector();
		SocketAddress address = new InetSocketAddress("127.0.0.1", 1234);

		handler = new EasyMessageHandler();
		connect(connector, address);
		String getUrl = "GET /pub/WWW/TheProject.html HTTP/1.1";
		System.out.println(Charset.defaultCharset());
		byte[] buffer = getUrl.getBytes(Charset.defaultCharset());
		session.write(buffer);
		session.getCloseFuture().join();
		session.close();
	}

	public boolean connect(SocketConnector connector, SocketAddress address) {
		if (session != null && session.isConnected()) {
			throw new IllegalStateException(
					"Already connected. Disconnect first.");
		}

		try {
			ConnectFuture future1 = connector.connect(address, handler);
			future1.join();
			if (!future1.isConnected()) {
				return false;
			}
			session = future1.getSession();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}