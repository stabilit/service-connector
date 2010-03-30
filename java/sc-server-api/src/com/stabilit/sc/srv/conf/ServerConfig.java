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
package com.stabilit.sc.srv.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author JTraber
 * 
 */
public class ServerConfig {

	private Properties props;
	private List<ServerConfigItem> serverConfigList;

	public ServerConfig() {
		super();
		serverConfigList = null;
		props = null;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public void load(String fileName) throws IOException {
		InputStream	is = ClassLoader.getSystemResourceAsStream(fileName);
		props = new Properties();
		props.load(is);

		String serverNames = props.getProperty("serverNames");

		String[] servers = serverNames.split(",|;");
		serverConfigList = new ArrayList<ServerConfigItem>();

		for (String serverName : servers) {
			ServerConfigItem serverConfig = new ServerConfigItem(serverName);

			serverConfigList.add(serverConfig);
			
			int port = Integer.parseInt((String) props.get(serverName + ".port"));

			serverConfig.setPort(port);
			serverConfig.setHost((String) props.get(serverName + ".host"));
			serverConfig.setCon((String) props.get(serverName + ".con"));
		}
	}

	public List<ServerConfigItem> getServerConfigList() {
		return serverConfigList;
	}
	
	public class ServerConfigItem {

		private String serverName;
		private int port;
		private String host;
		private String con;
		
		/**
		 * @param serverName
		 */
		public ServerConfigItem(String serverName) {
			this.serverName = serverName;
		}

		public String getServerName() {
			return serverName;
		}

		public void setServerName(String serverName) {
			this.serverName = serverName;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getCon() {
			return con;
		}

		public void setCon(String con) {
			this.con = con;
		}
	}
}
