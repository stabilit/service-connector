/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.sim.client.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stabilit.sc.config.ClientConfig;

/**
 * @author JTraber
 * 
 */
public class Config {

	private Properties props;
	private List<ClientConfig> clientConfigList;

	public Config() {
		super();
		clientConfigList = null;
		props = null;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public void load(String fileName) throws IOException {
		InputStream is = Config.class.getResourceAsStream(".." + File.separatorChar + fileName);
		props = new Properties();
		props.load(is);

		String serverNames = props.getProperty("serverNames");

		String[] servers = serverNames.split(",|;");
		clientConfigList = new ArrayList<ClientConfig>();

		for (String serverName : servers) {
			ClientConfig serverConfig = new ClientConfig();

			clientConfigList.add(serverConfig);
			
			int port = Integer.parseInt((String) props.get(serverName + ".port"));

			serverConfig.setPort(port);
			serverConfig.setHost((String) props.get(serverName + ".host"));
			serverConfig.setCon((String) props.get(serverName + ".con"));
		}
	}

	public List<ClientConfig> getClientConfigList() {
		return clientConfigList;
	}
}
