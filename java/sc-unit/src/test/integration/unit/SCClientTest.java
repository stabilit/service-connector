/**
 * 
 */
package unit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IFileService;
import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.conf.Constants;

/**
 * @author FJurnecka
 *
 */
public class SCClientTest {

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port = 8000;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		client = new SCClient();
	}
	
	@Test
	public void construtor_setInitialValues_valuesSet()
	{
		assertEquals(null, client.getHost());
		assertEquals(-1, client.getPort());
		assertEquals(Constants.DEFAULT_CLIENT_CON, client.getConnectionType());
		assertEquals(Constants.DEFAULT_KEEP_ALIVE_INTERVAL, client.getKeepAliveIntervalInSeconds());
		assertEquals(Constants.DEFAULT_MAX_CONNECTIONS, client.getMaxConnections());
		assertNotNull(client.getContext());		
	}
	
	@Test
	public void newFileService_NullParam_returnsIFileService()
	{
		assertTrue(client.newFileService(null) instanceof IFileService);
	}
	
	@Test
	public void newFileService_EmptyStringParam_returnsIFileService()
	{
		assertTrue(client.newFileService(new String()) instanceof IFileService);
	}

	@Test
	public void newFileService_ArbitraryStringParam_returnsIFileService()
	{
		assertTrue(client.newFileService(host) instanceof IFileService);
	}

	@Test
	public void newPublishService_NullParam_returnsIPublishService()
	{		
		assertTrue(client.newPublishService(null) instanceof IPublishService);
	}
	
	@Test
	public void newPublishService_EmptyStringParam_returnsIPublishService()
	{
		assertTrue(client.newPublishService("") instanceof IPublishService);
	}

	@Test
	public void newPublishService_ArbitraryStringParam_returnsIPublishService()
	{
		assertTrue(client.newPublishService(host) instanceof IPublishService);
	}

	@Test
	public void newSessionService_NullParam_returnsISessionService()
	{
		assertTrue(client.newSessionService(null) instanceof ISessionService);
	}
	
	@Test
	public void newSessionService_EmptyStringParam_returnsISessionService()
	{
		assertTrue(client.newSessionService("") instanceof ISessionService);
	}

	@Test
	public void newSessionService_ArbitraryStringParam_returnsISessionService()
	{
		assertTrue(client.newSessionService(host) instanceof ISessionService);
	}

	@Test
	public void setConnectionType_NullParam_returnNull()
	{
		((SCClient)client).setConnectionType(null);
		assertEquals(null, client.getConnectionType());
	}

	@Test
	public void setConnectionType_EmptyStringParam_returnEmptyString()
	{
		((SCClient)client).setConnectionType(new String());
		assertEquals("", client.getConnectionType());
	}

	@Test
	public void setConnectionType_OneCharParam_returnGivenParam()
	{
		((SCClient)client).setConnectionType("a");
		assertEquals("a", client.getConnectionType());
	}
	
	@Test
	public void setConnectionType_ArbitraryStringParam_returnGivenParam()
	{
		((SCClient)client).setConnectionType(host);
		assertEquals(host, client.getConnectionType());
	}
	
	@Test
	public void setMaxConnections_0Param_return0()
	{
		client.setMaxConnections(0);
		assertEquals(0, client.getMaxConnections());
	}
	
	@Test
	public void setMaxConnections_minParam_returnMin()
	{
		client.setMaxConnections(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, client.getMaxConnections());
	}
	
	@Test
	public void setMaxConnections_maxParam_returnMax()
	{
		client.setMaxConnections(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, client.getMaxConnections());
	}
	
	@Test
	public void setMaxConnections_minus1Param_returnMinus1()
	{
		client.setMaxConnections(-1);
		assertEquals(-1, client.getMaxConnections());
	}
	
	@Test
	public void setMaxConnections_1Param_return1()
	{
		client.setMaxConnections(1);
		assertEquals(1, client.getMaxConnections());
	}
}
