package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.ctrl.util.TestConstants;



public class SCServerDestroyServerTest {

	SCSessionServer server;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCSessionServer();
	}
	
	@Test
	public void destroyServer_withoutPreviousisListening_notListening()
	{
		server.destroy();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void destroyServer_withValidisListening_notListening() throws Exception
	{
		server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 1);
		assertEquals(true, server.isListening());
		server.destroy();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void startListeningDestroyServer_400Times_notListening() throws Exception
	{
		for (int i = 0; i < 400; i++) {
			server.startListener(TestConstants.HOST, TestConstants.PORT_HTTP, 1);
			assertEquals(true, server.isListening());
			server.destroy();
			assertEquals(false, server.isListening());
		}
	}
}
