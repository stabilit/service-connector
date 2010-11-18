/**
 * 
 */
package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;

/**
 * @author FJurnecka
 * 
 */
public class SCClientTest {

	private SCClient client;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void init() {
		client = null;
	}

	/**
	 * Description: Invoke Constructor with Host and Port<br>
	 * Expectation: Host and Port was set
	 */
	@Test
	public void t01_construtor() {
		client = new SCClient("localhost", 7000);
		assertEquals("Host not equal", "localhost", client.getHost());
		assertEquals("Port not equal", 7000, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t02_construtor() {
		client = new SCClient("localhost", 6000, ConnectionType.NETTY_TCP );
		assertEquals("Host not equal", "localhost", client.getHost());
		assertEquals("Port not equal", 6000, client.getPort());
		assertEquals("Connection Type not equal", ConnectionType.NETTY_TCP.getValue(), client.getConnectionType());
		assertNotNull(client);
	}

	
	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t03_construtor() {
		client = new SCClient(null, 6000);
		assertEquals("Host not equal", null, client.getHost());
		assertEquals("Port not equal", 6000, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke Constructor with Host, Port and connection Type<br>
	 * Expectation: Host, Port and connection Type was set
	 */
	@Test
	public void t04_construtor() {
		client = new SCClient(null, -1);
		assertEquals("Host not equal", null, client.getHost());
		assertEquals("Port not equal", -1, client.getPort());
		assertNotNull(client);
	}

	/**
	 * Description: Invoke setMaxConnections with 0 value<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t10_maxConnections() throws SCMPValidatorException {
		client = new SCClient("localhost", 6000 );
		client.setMaxConnections(0);
	}
	
	/**
	 * Description: Invoke setMaxConnections with value = Integer.MIN_VALUE<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t11_maxConnections() throws SCMPValidatorException {
		client = new SCClient("localhost", 6000 );
		client.setMaxConnections(Integer.MIN_VALUE);
	}

	/**
	 * Description: Invoke setMaxConnections with value = Integer.MAX_VALUE<br>
	 * Expectation: value = MAX was properly set
	 */
	@Test
	public void t12_maxConnections() throws SCMPValidatorException {
		client = new SCClient("localhost", 6000 );
		client.setMaxConnections(Integer.MAX_VALUE);
		assertEquals("MaxConnections not equal", Integer.MAX_VALUE, client.getMaxConnections());
	}

	/**
	 * Description: Invoke setMaxConnections with value = -1<br>
	 * Expectation: throws validation exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t13_maxConnections() throws SCMPValidatorException {
		client = new SCClient("localhost", 6000 );
		client.setMaxConnections(-1);
	}

	/**
	 * Description: Invoke setMaxConnections with value = 1<br>
	 * Expectation: value = 1 was properly set
	 */
	@Test
	public void t14_maxConnections() throws SCMPValidatorException {
		client = new SCClient("localhost", 6000 );
		client.setMaxConnections(1);
		assertEquals("MaxConnections not equal", 1, client.getMaxConnections());
	}
	
	/**
	 * Description: Invoke keep alive Interval with value = 0<br>
	 * Expectation: value = 0 was properly set
	 */
	@Test
	public void t20_keepAliveInterval() {
		client = new SCClient("localhost", 6000 );
		client.setKeepAliveIntervalInSeconds(0); // can be set before attach
		assertEquals("MaxConnections not equal", 0, client.getKeepAliveIntervalInSeconds());
	}
	
	/**
	 * Description: Invoke keep alive Interval with value = Integer.MAX_VALUE<br>
	 * Expectation: value = Integer.MAX_VALUE was properly set
	 */
	@Test
	public void t21_keepAliveInterval() {
		client = new SCClient("localhost", 6000 );
		client.setKeepAliveIntervalInSeconds(Integer.MAX_VALUE); // can be set before attach
		assertEquals("MaxConnections not equal", Integer.MAX_VALUE, client.getKeepAliveIntervalInSeconds());
	}

	/**
	 * Description: Invoke keep alive Interval with value = Integer.MIN_VALUE<br>
	 * Expectation: value = Integer.MMIN_VALUE was properly set
	 */
	@Test
	public void t22_keepAliveInterval() {
		client = new SCClient("localhost", 6000 );
		client.setKeepAliveIntervalInSeconds(Integer.MIN_VALUE); // can be set before attach
		assertEquals("MaxConnections not equal", Integer.MIN_VALUE, client.getKeepAliveIntervalInSeconds());
	}
}
	
