package org.serviceconnector.conf.apache.common.configuration;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class CommonConfigSample {

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
			System.out.println("a = " + a);
			System.out.println("b = " + b);
			System.out.println("c = " + c);
			System.out.println("d = " + d);
			System.out.println("e = " + e);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
