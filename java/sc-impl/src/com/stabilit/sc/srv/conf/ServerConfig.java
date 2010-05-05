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
package com.stabilit.sc.srv.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stabilit.sc.srv.config.IServerConfigItem;

/**
 * @author JTraber
 * 
 */
public class ServerConfig  {

	private Properties props;
	private List<ServerConfigItem> serverConfigList;

	public ServerConfig() {
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
	
	public class ServerConfigItem implements IServerConfigItem {

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
