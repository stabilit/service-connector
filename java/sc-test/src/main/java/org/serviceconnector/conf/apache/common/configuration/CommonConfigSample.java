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
package org.serviceconnector.conf.apache.common.configuration;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.log.Loggers;

public class CommonConfigSample {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	
	public static void main(String[] args) {
		String commonPropertiesFile = "org/serviceconnector/conf/apache/common/configuration/sc.common.properties";
		CompositeConfiguration config = new CompositeConfiguration();
		try {
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(commonPropertiesFile);
			config.addConfiguration(propertiesConfiguration);
			int a = config.getInt("a");
			int b = config.getInt("b");
			int c = config.getInt("c");
			int d = config.getInt("d");
			int e = config.getInt("e");
			testLogger.info("a = " + a);
			testLogger.info("b = " + b);
			testLogger.info("c = " + c);
			testLogger.info("d = " + d);
			testLogger.info("e = " + e);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
