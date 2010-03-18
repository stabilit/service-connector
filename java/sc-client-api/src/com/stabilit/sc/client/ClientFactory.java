package com.stabilit.sc.client;

import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;

public class ClientFactory extends Factory {

	public ClientFactory() {
		Client client = new Client();
	    this.factoryMap.put("default", client);
	}
	
	public IClient newInstance(ClientConfig clientConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IClient client = (IClient)factoryInstance;
		client.setClientConfig(clientConfig);
		return client;
	}
}
