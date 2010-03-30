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
package com.stabilit.sc.cln.config;

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
public class ClientConfig {

	private Properties props;
	private List<ClientConfigItem> clientConfigItemList;

	public ClientConfig() {
		super();
		clientConfigItemList = null;
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

		String serverNames = props.getProperty("connectionNames");

		String[] servers = serverNames.split(",|;");
		clientConfigItemList = new ArrayList<ClientConfigItem>();

		for (String serverName : servers) {
			ClientConfigItem clientConfigItem = new ClientConfigItem();

			clientConfigItemList.add(clientConfigItem);
			
			int port = Integer.parseInt((String) props.get(serverName + ".port"));

			clientConfigItem.setPort(port);
			clientConfigItem.setHost((String) props.get(serverName + ".host"));
			clientConfigItem.setCon((String) props.get(serverName + ".con"));
		}
	}

	public List<ClientConfigItem> getClientConfigList() {
		return clientConfigItemList;
	}
	
	public ClientConfigItem getClientConfig() {
		return clientConfigItemList.get(0);
	}
	
	public class ClientConfigItem {

		private int port;
		private String host;
		private String con;

		public ClientConfigItem() {
		}
		
		public ClientConfigItem(String host, int port, String con) {
			super();
			this.port = port;
			this.host = host;
			this.con = con;
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
