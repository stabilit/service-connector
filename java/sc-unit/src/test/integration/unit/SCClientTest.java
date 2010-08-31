/**
 * 
 */
package unit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.IFileService;
import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.conf.Constants;

/**
 * @author FJurnecka
 *
 */
public class SCClientTest {

	private ISCClient client;

	private static final String host = "localhost";
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
		assertEquals(false, client.isAttached());
		assertNotNull(client.getContext());		
	}
	
	@Test(expected = InvalidParameterException.class)
	public void newFileService_NullParam_throwsInvalidParamException() throws Exception
	{
		client.newFileService(null);
	}
	
	@Test
	public void newFileService_EmptyStringParam_returnsIFileService() throws Exception
	{
		assertTrue(client.newFileService(new String()) instanceof IFileService);
	}

	@Test
	public void newFileService_ArbitraryStringParam_returnsIFileService() throws Exception
	{
		assertTrue(client.newFileService(host) instanceof IFileService);
	}

	@Test(expected = InvalidParameterException.class)
	public void newPublishService_NullParam_throwsInvalidParamException() throws Exception
	{		
		client.newPublishService(null);
	}
	
	@Test
	public void newPublishService_EmptyStringParam_returnsIPublishService() throws Exception
	{
		assertTrue(client.newPublishService("") instanceof IPublishService);
	}

	@Test
	public void newPublishService_ArbitraryStringParam_returnsIPublishService() throws Exception
	{
		assertTrue(client.newPublishService(host) instanceof IPublishService);
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
	public void setConnectionType_whiteCharParam_returnEmptyString()
	{
		((SCClient)client).setConnectionType(" ");
		assertEquals(" ", client.getConnectionType());
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
	
	@Test(expected = SCMPValidatorException.class)
	public void setMaxConnections_0Param_throwsSCMPValidatorException() throws SCMPValidatorException
	{
		client.setMaxConnections(0);
	}
	
	@Test(expected = SCMPValidatorException.class)
	public void setMaxConnections_minParam_throwsInvalidParamException() throws SCMPValidatorException
	{
		client.setMaxConnections(Integer.MIN_VALUE);
	}
	
	@Test
	public void setMaxConnections_maxParam_returnMax() throws SCMPValidatorException
	{
		client.setMaxConnections(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, client.getMaxConnections());
	}
	
	@Test(expected = SCMPValidatorException.class)
	public void setMaxConnections_minus1Param_throwsInvalidParamException() throws SCMPValidatorException
	{
		client.setMaxConnections(-1);
	}
	
	@Test
	public void setMaxConnections_1Param_return1() throws SCMPValidatorException
	{
		client.setMaxConnections(1);
		assertEquals(1, client.getMaxConnections());
	}
}
