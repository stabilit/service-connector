package integration;


import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.net.req.ConnectionPoolConnectException;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

public class RegisterServiceServerToSC {

	private Process p;
	private ISCServer server;
	private Exception ex;


	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			String userDir = System.getProperty("user.dir");		    
			String command = "cmd /c start java -Dlog4j.configuration=file:" + userDir +
			  "\\src\\test\\resources\\log4j.properties -jar " + userDir +
			  "\\..\\service-connector\\target\\sc.jar -filename " + userDir +
			  "\\src\\test\\resources\\scIntegration.properties";
			
			Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void oneTimeTearDown() {
		SCClient endClient = new SCClient();
		try {
			endClient.attach("localhost", 8080);
			((SCClient) endClient).killSC();
		} catch (SCServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}

	@After
	public void tearDown() throws Exception {
		p.destroy();
	}
	
//	region hostName == "localhost" (set as only one in 
//	scIntegration.properties), all ports
	@Test
	public void registerService_withoutStartListener_throwsException() {
		try {
		server.registerService("localhost", 8080, "Name", 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof InvalidActivityException);
	}
	
	@Test
	public void registerService_withStartListenerToSameHostAndPort_throwsException() throws Exception {
		server.startListener("localhost", 8080, 1);
		try {
			server.registerService("localhost", 8080, "Name", 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void registerService_withStartListenerToSameHostDifferentPort_throwsException() throws Exception {
		server.startListener("localhost", 8080, 1);
		try {
			server.registerService("localhost", 9000, "Name", 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}
	
	@Test
	public void registerService_withStartListenerToDifferentHostSamePort_throwsException() throws Exception {
		server.startListener("localhost", 8080, 1);
		try {
			server.registerService("host", 8080, "Name", 1, new CallBack());
		} catch (Exception e) {
			ex = e;
		}
		assertEquals(true, ex instanceof SCServiceException);
	}

	
	@Test(expected = SCServiceException.class)
	public void registerService_hostLocalhostPort9000_registerServiceed() throws Exception {
		try {
			server.registerService("localhost", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}
	
	@Test
	public void registerService_changeConnectionTypeHostLocalhostPort9000_registerServiceed() throws Exception {
		((SCClient)server).setConnectionType("netty.tcp");
		server.registerService("localhost", 9000);
		assertEquals(true, server.isRegistered());
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostLocalhostPort0_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("localhost", 0);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostLocalhostPortMinus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("localhost", -1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostLocalhostPort1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("localhost", 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostLocalhostPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("localhost", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}

	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostLocalhostPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("localhost", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostLocalhostPortIntMin_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("localhost", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostLocalhostPortIntMax_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("localhost", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	
//	region end
//	region hostName == "null", all ports
	
	
	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, 8080);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, 9000);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPort0_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, 0);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, -1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPort1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService(null, 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService(null, 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostNullPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.registerService(null, Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

//	region end
//	region hostName == "", all ports
	
	
	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostEmptyPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostEmptyPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostEmptyPort0_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", 0);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostEmptyPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", -1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostEmptyPort1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostEmptyPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostEmptyPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostEmptyPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostEmptyPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}
	
//	region end
//	region hostName == "a", all ports
	
	
	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostAPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostAPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostAPort0_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", 0);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostAPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", -1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostAPort1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostAPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("a", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostAPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("a", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostAPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostAPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("a", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}
	
	
//	region end
//	region hostName == "The quick brown fox jumps over a lazy dog.", all ports

	
	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostArbitraryPort8080_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 8080);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostArbitraryPort9000_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 9000);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostArbitraryPort0_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 0);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostArbitraryPortMinus1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", -1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostArbitraryPort1_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = ConnectionPoolConnectException.class)
	public void registerService_hostArbitraryPortMaxAllowed_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 0xFFFF);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostArbitraryPortMaxAllowedPlus1_notAttachedThrowsException()
			throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", 0xFFFF + 1);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostArbitraryPortIntMin_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", Integer.MIN_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}

	@Test(expected = InvalidParameterException.class)
	public void registerService_hostArbitraryPortIntMax_notAttachedThrowsException() throws Exception {
		try {
			server.registerService("The quick brown fox jumps over a lazy dog.", Integer.MAX_VALUE);
		} catch (Exception e) {
			assertEquals(false, server.isRegistered());
			throw e;
		}
	}
	
	
//	region end
	
	private class CallBack implements ISCServerCallback {		
	}

}
