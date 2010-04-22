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
package com.stabilit.sc.cln.config;

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
