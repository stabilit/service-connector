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
package com.stabilit.sc.app.client.mina.http;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import com.stabilit.sc.app.client.PerformanceOneMinaClient;

public class HttpClientHandler extends IoHandlerAdapter {

	private static IoFilter LOGGING_FILTER = new LoggingFilter();
	private static final byte[] CRLF = new byte[] { 0x0D, 0x0A };

	private static IoFilter CODEC_FILTER = new ProtocolCodecFilter(
			new TextLineCodecFactory());

	public void sessionCreated(IoSession session) throws Exception {
		session.getFilterChain().addLast("codec", CODEC_FILTER);
		// session.getFilterChain().addLast("logger", LOGGING_FILTER);
	}

	public void sessionOpened(IoSession session) throws Exception {
		// System.out.println("Session opened on client : " +
		// System.currentTimeMillis());
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		sendAndReceive(session);
		if (PerformanceOneMinaClient.getInstance().minaHandlerMethod()) {
			long endTime = System.currentTimeMillis();
			long neededTime = endTime - PerformanceOneMinaClient.startTime;
			System.out.println("Job Done in: " + neededTime + " Ms");
			double neededSeconds = neededTime / 1000;
			System.out
					.println((PerformanceOneMinaClient.numberOfMsgMina * 1 / neededSeconds)
							+ " Messages in 1 second!");
			System.out.println("Anzahl clients: " + 1);
			System.out.println("Anzahl Messages pro client: "
					+ PerformanceOneMinaClient.numberOfMsgMina);
		}
		// System.out.println("sysout messageRecieved on client: " + (String)
		// message);
	}

	public void sessionClosed(IoSession session) throws Exception {
		// System.out.println("Session closed on client  : " +
		// System.currentTimeMillis());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}

	public void sendAndReceive(IoSession session) throws Exception {
		CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
		ByteBuffer buf = ByteBuffer.allocate(128);
		try {
			String httpRequest = "GET http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/123456789/TheProject.html HTTP/1.1 \n";
			buf.setAutoExpand(true);
			buf.putString(httpRequest, encoder);
			buf.put(CRLF);
			buf.put(CRLF);
			buf.put(CRLF);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		buf.flip();
		session.write(buf);
	}
}