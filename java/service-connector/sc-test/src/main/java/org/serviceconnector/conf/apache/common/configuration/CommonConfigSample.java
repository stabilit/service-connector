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

import java.util.List;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.log.Loggers;

public class CommonConfigSample {

	/** The Constant testLogger. */
	protected static final Logger testLogger = LoggerFactory.getLogger(Loggers.TEST.getValue());

	public static void main(String[] args) {
		String commonPropertiesFile = "org/serviceconnector/conf/apache/common/configuration/sc.common.properties";
		CompositeConfiguration config = new CompositeConfiguration();
		try {
			PropertiesConfiguration propertiesConfiguration = buildPropertiesConfigurationWithFile(commonPropertiesFile);
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
	
	private static PropertiesConfiguration buildPropertiesConfigurationWithFile(String configFile) throws ConfigurationException {
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
				.configure(new Parameters().properties().setFileName(configFile).setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
		return builder.getConfiguration();
	}
}
