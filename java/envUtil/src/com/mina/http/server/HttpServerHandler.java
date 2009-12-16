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
package com.mina.http.server;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

/**
 * An {@link IoHandler} for HTTP.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007)
 *          $
 */
public class HttpServerHandler extends IoHandlerAdapter {
	@Override
	public void sessionOpened(IoSession session) {
		// set idle time to 60 seconds
		// session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		// Check that we can service the request context
		HttpResponseMessage response = new HttpResponseMessage();
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		response.appendBody("CONNECTED");

		System.out.println("Message Content");
		System.out.println(message);
		System.out.println("Message Length: "
				+ ((HttpRequestMessage) message).getContext());

		if (response != null) {
			session.write(response).join();
		}
		session.close();
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		// SessionLog.info(session, "Disconnecting the idle.");
		session.close();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		session.close();
	}
}
