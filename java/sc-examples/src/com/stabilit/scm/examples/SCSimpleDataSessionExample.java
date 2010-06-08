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
package com.stabilit.scm.examples;

import com.stabilit.scm.cln.service.IServiceConnector;
import com.stabilit.scm.cln.service.IServiceConnectorContext;
import com.stabilit.scm.cln.service.IServiceConnectorFactory;
import com.stabilit.scm.cln.service.ISession;
import com.stabilit.scm.cln.service.ISessionContext;
import com.stabilit.scm.cln.service.ServiceConnectorFactory;
import com.stabilit.scm.common.listener.DefaultStatisticsListener;
import com.stabilit.scm.common.listener.StatisticsPoint;

public class SCSimpleDataSessionExample {

	public static void main(String[] args) {
		SCSimpleDataSessionExample scSingleDataServiceApiExample = new SCSimpleDataSessionExample();
		scSingleDataServiceApiExample.runExample();
	}

	public void runExample() {
		IServiceConnector sc1 = null;
		try {
			IServiceConnectorFactory scFactory = new ServiceConnectorFactory();
			sc1 = scFactory.createServiceConnector("localhost", 8080);
			sc1.setAttribute("keep-alive", 100);

			sc1.connect();

			IServiceConnectorContext sc1Context1 = sc1.getSCContext();
			String host = sc1Context1.getHost();
			String port = sc1Context1.getPort();

			ISession dataSessionA = sc1.createDataSession("simulation");
			ISessionContext sessionContextA = dataSessionA.getSessionContext();

			String serviceName = sessionContextA.getServiceName();

			byte[] data = new byte[1024];
			dataSessionA.setData(data);
			dataSessionA.setMessagInfo("message info");

			Object resp = dataSessionA.invoke();
			dataSessionA.deleteSession();

			sc1.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sc1.disconnect();
			} catch (Exception e) {
				sc1 = null;
			}
		}
	}
	
	public static class WithStatistics {
		public static void main(String[] args) {
			SCSimpleDataSessionExample scSingleDataServiceApiExample = new SCSimpleDataSessionExample();
			scSingleDataServiceApiExample.runStatistics();
		}
	}
	
	public void runStatistics() {
		DefaultStatisticsListener defaultStatisticsListener = new DefaultStatisticsListener();
		StatisticsPoint.getInstance().addListener(defaultStatisticsListener);
		runExample();
		StatisticsPoint.getInstance().removeListener(defaultStatisticsListener);
		System.out.println(defaultStatisticsListener);
	}
}
