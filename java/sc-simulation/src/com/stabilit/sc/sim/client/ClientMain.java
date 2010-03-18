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
package com.stabilit.sc.sim.client;

import java.util.List;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.MsgType;
import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.sim.config.ConfigForClients;

/**
 * @author JTraber
 *
 */
public class ClientMain {
	
	public static void main(String[] args) throws Exception {
		ConfigForClients config = new ConfigForClients();
		config.load("sc-client-api.properties");
		
		List<ClientConfig> clientConfigList = config.getClientConfigList();
		ClientFactory clientFactory = new ClientFactory();
		for (ClientConfig clientConfig : clientConfigList) {
			IClient client = clientFactory.newInstance(clientConfig);
		
			client.connect();
			SCMP scmp = new SCMP();
			scmp.setMessageId(MsgType.ECHO.getName());
			EchoMessage echo = new EchoMessage();
			scmp.setBody(echo);
			SCMP result = client.sendAndReceive(scmp);
			
			System.out.println(result.getBody());
			client.disconnect();
		}
	}
}
