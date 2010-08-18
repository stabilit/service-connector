package unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.SCServer;

public class SCServerTest {

	private ISCServer server;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		server = new SCServer();
	}

	@Test
	public void setConnectionType_nullParam_null() {
		((SCServer) server).setConnectionType(null);
		assertEquals(null, server.getConnectionType());
	}

	@Test
	public void setConnectionType_emptyString_emptyString() {
		((SCServer) server).setConnectionType("");
		assertEquals("", server.getConnectionType());
	}

	@Test
	public void setConnectionType_oneCharString_givenString() {
		((SCServer) server).setConnectionType("a");
		assertEquals("a", server.getConnectionType());
	}

	@Test
	public void setConnectionType_arbitraryString_givenString() {
		((SCServer) server).setConnectionType("aaa");
		assertEquals("aaa", server.getConnectionType());
	}

	@Test
	public void setConnectionType_shortMaxLengthString_givenString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCServer) server).setConnectionType(sb.toString());
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

	@Test(expected = InvalidParameterException.class)
	public void setMaxSessions_0_invalidParamException() {
		server.setMaxSessions(0);
	}

	@Test(expected = InvalidParameterException.class)
	public void setMaxSessions_minus1_invalidParamException() {
		server.setMaxSessions(-1);
	}

	@Test(expected = InvalidParameterException.class)
	public void setMaxSessions_integerMin_invalidParamException() {
		server.setMaxSessions(Integer.MIN_VALUE);
	}

	@Test
	public void setMaxSessions_1_1() {
		server.setMaxSessions(1);
		assertEquals(1, server.getMaxSessions());
	}

	@Test
	public void setMaxSessions_integerMax_integerMax() {
		server.setMaxSessions(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, server.getMaxSessions());
	}

}
