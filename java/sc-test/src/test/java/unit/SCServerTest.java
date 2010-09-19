package unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.srv.ISCSessionServer;
import org.serviceconnector.srv.SCSessionServer;


public class SCServerTest {

	private ISCSessionServer server;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCSessionServer();
	}
	
	@Test
	public void constructor_noAction_defaultParameters(){
		assertEquals(false, server.isListening());
		assertEquals(Constants.DEFAULT_SERVER_CON, server.getConnectionType());
		assertEquals(true, server.isImmediateConnect());
		assertEquals(null, server.getHost());
		assertEquals(Constants.DEFAULT_KEEP_ALIVE_INTERVAL, server.getKeepAliveIntervalInSeconds());
		assertEquals(-1, server.getPort());		
	}

	@Test
	public void setConnectionType_nullParam_null() {
		((SCSessionServer) server).setConnectionType(null);
		assertEquals(null, server.getConnectionType());
	}

	@Test
	public void setConnectionType_emptyString_emptyString() {
		((SCSessionServer) server).setConnectionType("");
		assertEquals("", server.getConnectionType());
	}

	@Test
	public void setConnectionType_whiteSpaceString_emptyString() {
		((SCSessionServer) server).setConnectionType(" ");
		assertEquals(" ", server.getConnectionType());
	}

	@Test
	public void setConnectionType_oneCharString_givenString() {
		((SCSessionServer) server).setConnectionType("a");
		assertEquals("a", server.getConnectionType());
	}

	@Test
	public void setConnectionType_arbitraryString_givenString() {
		((SCSessionServer) server).setConnectionType("aaa");
		assertEquals("aaa", server.getConnectionType());
	}

	@Test
	public void setConnectionType_shortMaxLengthString_givenString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCSessionServer) server).setConnectionType(sb.toString());
		assertEquals(sb.toString(), server.getConnectionType());
	}

	@Test
	public void setImmediateConnect_true_true() {
		server.setImmediateConnect(true);
		assertEquals(true, server.isImmediateConnect());
	}

	@Test
	public void setImmediateConnect_false_false() {
		server.setImmediateConnect(false);
		assertEquals(false, server.isImmediateConnect());
	}

}
