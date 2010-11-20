/*
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
 */
package org.serviceconnector.test;

import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnetor.TestConstants;

public class ThreadTest {

	private static ProcessesController ctrl;
	private static Process scProcess;
	private static Process srvProcess;

	public static void main(String[] args) throws Exception {
		try {
			ThreadTest test = new ThreadTest();
			test.run();
		} finally {
			scProcess.destroy();
			srvProcess.destroy();
		}
	}

	public void run() throws Exception {
		ctrl = new ProcessesController();
		scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		srvProcess = ctrl.startServer(TestConstants.sessionSrv, TestConstants.log4jSrvProperties, 9001, TestConstants.PORT_TCP, 100,
				new String[] { TestConstants.sessionServiceName });
	}
}
