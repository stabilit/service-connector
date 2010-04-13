package com.stabilit.sc.srv.client;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.config.ClientConfig.ClientConfigItem;
import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;

public class SCClientFactory extends Factory {

	public SCClientFactory() {
		IClient client = new SCClient();
		this.factoryMap.put("default", client);
	}

	public IClient newInstance(String host, int port, String con) {
		IFactoryable factoryInstance = this.newInstance();
		IClient client = (IClient) factoryInstance;
		ClientConfigItem clientConfigItem = new ClientConfig().new ClientConfigItem(host, port, con);
		client.setClientConfig(clientConfigItem);
		return client;
	}
}
