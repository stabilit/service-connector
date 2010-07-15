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
package com.stabilit.scm.srv.ps;

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.srv.ISCPublishServer;

public class PublishServer {

	private static ISCPublishServer sc = null;
	private static boolean killed = false;

	public static void main(String[] args) throws Exception {
		PublishServer.runExample();
	}

	public static void beginPublish() {
		killed = false;
		Thread thread = new Thread(new PublishRun());
		thread.start();
	}

	public static void endPublish() {
		killed = true;
	}

	public static void runExample() {
		sc = new SCPublishServer("localhost", 9000, "netty.tcp");

		try {
			sc.startServer("publish-server.properties");
			sc.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class PublishRun implements Runnable {
		@Override
		public void run() {
			int index = 0;
			while (!PublishServer.killed) {
				try {
					Thread.sleep(20000);
					Object data = "publish message nr " + ++index;
					String mask = "AVSD-----";
					PublishServer.sc.publish(mask, data);
				} catch (Exception e) {
					ExceptionPoint.getInstance().fireException(this, e);
				}
				System.out.println("publish");

			}
		}
	}
}