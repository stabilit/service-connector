package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.srv.ISCSessionServer;
import org.serviceconnector.srv.SCSessionServer;



public class SCServerDestroyServerTest {

	ISCSessionServer server;
	
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
		server.destroyServer();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void destroyServer_withValidisListening_notListening() throws Exception
	{
		server.startListener("localhost", 8080, 1);
		assertEquals(true, server.isListening());
		server.destroyServer();
		assertEquals(false, server.isListening());
	}
	
	@Test
	public void startListeningDestroyServer_500Times_notListening() throws Exception
	{
		for (int i = 0; i < 500; i++) {
			server.startListener("localhost", 8080, 1);
			assertEquals(true, server.isListening());
			server.destroyServer();
			assertEquals(false, server.isListening());
		}
	}
}
