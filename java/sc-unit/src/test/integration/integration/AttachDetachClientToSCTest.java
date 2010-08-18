package integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.sc.SC;


public class AttachDetachClientToSCTest {

	private ISC sc;
	private ISCClient client;
	
	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SC.main(new String[] { "-filename", "scIntegration.properties" });
		client = new SCClient();
	}

	@Test(expected = SCServiceException.class)
	public void attach_twiceSameParams_throwsExceptionAttached() throws Exception
	{
		client.attach(host, port8080);
		try {
			client.attach(host, port8080);
		} catch (Exception e) {
			assertEquals(true, client.isAttached());
			throw e;
		}
	}
	
	@Test(expected = SCServiceException.class)
	public void attach_twiceDifferentParamsHttpFirst_throwsExceptionAttached() throws Exception
	{
		client.attach(host, port8080);
		try {
			((SCClient)client).setConnectionType("netty.tcp");
			client.attach(host, port9000);
		} catch (Exception e) {
			assertEquals(true, client.isAttached());
			throw e;
		}
	}
	
	@Test(expected = SCServiceException.class)
	public void attach_twiceDifferentParamsTcpFirst_throwsExceptionAttached() throws Exception
	{
		((SCClient)client).setConnectionType("netty.tcp");
		client.attach(host, port9000);
		try {
			client.attach(host, port8080);			
		} catch (Exception e) {
			assertEquals(true, client.isAttached());
			throw e;
		}
	}
	
	@Test
	public void detach_withoutAttach_notAttached() throws Exception
	{
		client.detach();
		assertEquals(false, client.isAttached());
	}
	
	@Test
	public void detach_validAttachPort8080_notAttached() throws Exception
	{
		client.attach(host, port8080);
		client.detach();
		assertEquals(false, client.isAttached());
	}
	
	@Test
	public void detach_validAttachPort9000_notAttached() throws Exception
	{
		((SCClient)client).setConnectionType("netty.tcp");
		client.attach(host, port9000);
		client.detach();
		assertEquals(false, client.isAttached());
	}
	
	@Test
	public void detach_afterDoubleAttemptedAttach_notAttached() throws Exception
	{
		client.attach(host, port8080);
		try {
			((SCClient)client).setConnectionType("netty.tcp");
			client.attach(host, port9000);
		} catch (Exception e) {
			client.detach();
			assertEquals(false, client.isAttached());
		}
	}
	
	@Test
	public void attachDetach_cycle100Times_notAttached() throws Exception
	{
		for (int i = 0; i < 500; i++) {
			client.attach(host, port8080);
			client.detach();
		}
		assertEquals(false, client.isAttached());
	}
}
