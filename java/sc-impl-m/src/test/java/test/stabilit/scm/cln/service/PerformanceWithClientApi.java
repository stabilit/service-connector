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
package test.stabilit.scm.cln.service;

import com.stabilit.scm.common.service.ISCClient;

/**
 * The Class PerformanceWithClientApi. Test the performance on client API Layer.
 */
public class PerformanceWithClientApi {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		PerformanceWithClientApi.runExample();
	}

	/**
	 * Run example.
	 */
	public static void runExample() {
		ISCClient sc = null;
		try {
//			sc = ServiceConnectorFactory.newInstance("localhost", 8080);
//			sc.setAttribute("keepAliveInterval", 60);
//			sc.setAttribute("keepAliveTimeout", 10);
//			sc.setAttribute("compression", false);
//
//			// connects to SC, starts observing connection
//			sc.attach();
//
//			ISessionService dataSessionA = sc.newSessionService("simulation");
//			dataSessionA.createSession("sessionInfo");
//
//			double anzMsg = 100000;
//			byte[] buffer = new byte[128];
//
//			double startTime = System.currentTimeMillis();
//			for (int i = 0; i < anzMsg; i++) {
//				byte[] data = new byte[128];
//				SCMessage message = new SCMessage(data);
//				Object resp = dataSessionA.execute(message);
//			}
//			double endTime = System.currentTimeMillis();
//
//			double neededTime = endTime - startTime;
//			System.out.println("Performance Test");
//			System.out.println("Anz msg pro sec: " + anzMsg / ((neededTime / 1000)));
//
//			dataSessionA.deleteSession();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
	}
}
