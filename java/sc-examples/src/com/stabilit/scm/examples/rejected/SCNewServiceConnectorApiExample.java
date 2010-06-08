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
package com.stabilit.scm.examples.rejected;

import com.stabilit.scm.cln.service.IService;
import com.stabilit.scm.cln.service.IServiceBuilder;
import com.stabilit.scm.cln.service.IServiceConnector;
import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.cln.service.ISessionContext;
import com.stabilit.scm.cln.service.ServiceConnector;

/**
 * @author JTraber
 */
public class SCNewServiceConnectorApiExample {

	public static void main(String[] args) throws Exception {
		SCNewServiceConnectorApiExample newServiceConnectorApiExample = new SCNewServiceConnectorApiExample();
		newServiceConnectorApiExample.runExample();
	}
	
	public void runExample() throws Exception {

		IServiceConnector sc1 = new ServiceConnector("localhost", 8080);
		IServiceBuilder sb1 = sc1.newDataServiceBuilder();
		IService dataService1 = sb1.createService("simulation");
		
		IServiceContext serviceContext1 = dataService1.getServiceContext();
		ISessionContext sessionContext1 = dataService1.getSessionContext();

		byte[] data = new byte[1024];
		dataService1.setMessagInfo("message info");
		dataService1.setRequestBody(data);		
		Object reply = dataService1.invoke();

		dataService1.destroyService();
	}
}
