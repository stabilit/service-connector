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
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.cln.service.ISCSubscription;
import com.stabilit.scm.common.service.ServiceConnectorFactory;

public class SCSimplePublishingServiceExample {

//	public static void main(String[] args) {
//		SCSimplePublishingServiceExample.runExample();
//	}
//
//	public static void runExample() {
//		IClientServiceConnector sc = null;
//		try {
//			sc = ServiceConnectorFactory.newClientInstance("localhost", 8080);
//			sc.setAttribute("keepAliveInterval", 60);
//			sc.setAttribute("keepAliveTimeout", 10);
//			sc.setAttribute("compression", false);
//
//			// connects to SC, starts observing connection
//			sc.connect();
//
//			String mask = "ABC-------"; // must not contain % sign
//			SCExampleMessageHandler messageHandler = new SCExampleMessageHandler();
//			ISCSubscription subscriptionA = sc.newSubscription("publish", messageHandler, mask);
//
//			subscriptionA.changeSubscription(mask);
//			// deletes the session
//			subscriptionA.unsubscribe();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				// disconnects from SC
//				sc.disconnect();
//			} catch (Exception e) {
//				sc = null;
//			}
//		}
//	}
}