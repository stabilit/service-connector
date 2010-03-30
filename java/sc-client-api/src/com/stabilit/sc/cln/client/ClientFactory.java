package com.stabilit.sc.cln.client;

import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;

public class ClientFactory extends Factory {

	public ClientFactory() {
		Client client = new Client();
		this.factoryMap.put("default", client);
	}

	public IClient newInstance(ClientConfigItem clientConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IClient client = (IClient) factoryInstance;
		client.setClientConfig(clientConfig);
		return client;
	}

	public IClient newInstance(String host, int port, String con) {
		IFactoryable factoryInstance = this.newInstance();
		IClient client = (IClient) factoryInstance;
		ClientConfigItem clientConfigItem = new ClientConfig().new ClientConfigItem(host, port, con);
		client.setClientConfig(clientConfigItem);
		return client;
	}
}
