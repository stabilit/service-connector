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
package com.stabilit.scm;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.stabilit.scm.cmd.factory.impl.ServiceConnectorCommandFactory;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.registry.ClientRegistry;
import com.stabilit.scm.registry.ServiceRegistry;
import com.stabilit.scm.registry.SessionRegistry;
import com.stabilit.scm.server.SCResponderFactory;
import com.stabilit.scm.srv.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.conf.ResponderConfig;
import com.stabilit.scm.srv.conf.ResponderConfig.ResponderConfigItem;
import com.stabilit.scm.srv.config.IResponderConfigItem;
import com.stabilit.scm.srv.res.IResponder;

/**
 * The Class ServiceConnector. Starts the core (responders) of the Service Connector.
 * 
 * @author JTraber
 */
public final class ServiceConnector {

	/**
	 * Instantiates a new service connector.
	 */
	private ServiceConnector() {
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		ServiceConnector.run();
	}

	/**
	 * Run SC responders.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run() throws Exception {
		ResponderConfig config = new ResponderConfig();
		config.load("sc.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new ServiceConnectorCommandFactory());
		}

		ServiceConnector.initializeJMXStuff();

		List<ResponderConfigItem> respConfigList = config.getResponderConfigList();
		SCResponderFactory respFactory = new SCResponderFactory();
		for (IResponderConfigItem respConfig : respConfigList) {
			IResponder resp = respFactory.newInstance(respConfig);
			try {
				resp.create();
				resp.runAsync();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(ServiceConnector.class, e);
			}
		}
	}

	/**
	 * Initialize jmx stuff.
	 */
	private static void initializeJMXStuff() {
		try {
			// Necessary to make access for JMX client available
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mxbeanNameConnReg = new ObjectName("com.stabilit.scm.registry:type=ClientRegistry");
			ObjectName mxbeanNameSessReg = new ObjectName("com.stabilit.scm.registry:type=SessionRegistry");
			ObjectName mxbeanNameServReg = new ObjectName("com.stabilit.scm.registry:type=ServiceRegistry");

			// Register the Queue Sampler MXBean
			mbs.registerMBean(ClientRegistry.getCurrentInstance(), mxbeanNameConnReg);
			mbs.registerMBean(SessionRegistry.getCurrentInstance(), mxbeanNameSessReg);
			mbs.registerMBean(ServiceRegistry.getCurrentInstance(), mxbeanNameServReg);
		} catch (Throwable th) {
			ExceptionPoint.getInstance().fireException(ServiceConnector.class, th);
		}
	}
}
