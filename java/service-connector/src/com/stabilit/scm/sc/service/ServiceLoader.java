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
package com.stabilit.scm.sc.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.sc.registry.ServiceRegistry;

/**
 * @author JTraber
 */
public class ServiceLoader {

	/**
	 * Loads services from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void load(String fileName) throws IOException {
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		Properties props = new Properties();
		props.load(is);

		String serviceNamesString = props.getProperty(IConstants.SERVICE_NAMES);
		String[] serviceNames = serviceNamesString.split(IConstants.COMMA_OR_SEMICOLON);

		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		for (String serviceName : serviceNames) {
			Service service = new Service(serviceName);
			
			
			// TODO verify with jan
			// service.setLocation(props.get(serviceName + IConstants.LOCATION_QUALIFIER));
			// service.setType(props.get(serviceName + IConstants.TYPEs_QUALIFIER));
			
			serviceRegistry.addService(service.getServiceName(), service);
			
		}
	}
}
