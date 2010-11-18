package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;


/**
 * @author FJurnecka
 * 
 */

public class SCMessageTest {

	private SCMessage message;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		message = new SCMessage();
	}

	/**
	 * Description:	Check empty message<br>
	 * Expectation:	all values are empty
	 */
	@Test
	public void t01_construtor() {
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
	}

	/**
	 * Description:	Create empty message with Null-DataParameter<br>
	 * Expectation:	all values are empty
	 */
	@Test
	public void t02_constructor() {
		message = new SCMessage();
		message.setData(null);
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
		assertEquals(false, message.isFault());
	}

	@Test(expected = InvalidParameterException.class)
	public void t03_constructor() {
		Object obj = new Object();
		message = new SCMessage();
		message.setData(obj);
	}

	@Test
	public void t10_Compressed() {
		message.setCompressed(true);
		assertEquals(true, message.isCompressed());
	}

	@Test
	public void t11_Compressed() {
		message.setCompressed(false);
		assertEquals(false, message.isCompressed());
	}

	/**
	 * _emptyObject_emptyObject
	 */
	@Test(expected = InvalidParameterException.class)
	public void t20_Data() {
		Object obj = new Object();
		message.setData(obj);
	}

	/**
	 * _arbitraryString_givenString
	 */
	@Test
	public void t21_Data() {
		message.setData("The quick brown fox jumps over a lazy dog.");
		assertEquals("The quick brown fox jumps over a lazy dog.", message
				.getData());
	}

	/**
	 * _1MBArray_allocated1MBArray
	 */
	@Test
	public void t22_Data() {
		message.setData(new byte[1048576]);
		assertEquals(1048576, ((byte[]) message.getData()).length);
	}

	/**
	 * _nullParam_acceptValue
	 */
	@Test
	public void t30_MessageInfo() {
		message.setMessageInfo(null);
		assertEquals(null, message.getMessageInfo());
	}

	/**
	 * _emptyParam_throwInvalidParamException
	 */
	@Test(expected = InvalidParameterException.class)
	public void t31_MessageInfo() {
		message.setMessageInfo("");
	}
	
	/**
	 * _whiteCharParam_throwInvalidParamException
	 */
	@Test
	public void t32_MessageInfo() {
		message.setMessageInfo(" ");
		assertEquals(" ", message.getMessageInfo());
	}

	/**
	 * _sinlgeCharParam_length1MessageInfo
	 */
	@Test
	public void t33_MessageInfo() {
		message.setMessageInfo("a");
		assertEquals("a", message.getMessageInfo());
		assertEquals(1, message.getMessageInfo().length());
	}

	/**
	 * _256CharParam_length256MessageInfo
	 */
	@Test
	public void t34_MessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
		assertEquals(sb.toString(), message.getMessageInfo());
		assertEquals(256, message.getMessageInfo().length());
	}

	/**
	 * _257CharParam_throwInvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void t35_MessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	/**
	 * @Test(expected = InvalidParameterException.class)
	 */
	public void t36_setMessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	/**
	 * _nullParam_nullSessionId
	 */
	@Test
	public void t40_SessionId() {
		((SCMessage) message).setSessionId(null);
		assertEquals(null, message.getSessionId());
	}

	/**
	 * _emptyString_emptySessionId
	 */
	@Test
	public void t41_SessionId() {
		((SCMessage) message).setSessionId("");
		assertEquals("", message.getSessionId());
	}

	/**
	 * _emptyString_emptySessionId
	 */
	@Test
	public void t42_SessionId()
	{
		((SCMessage) message).setSessionId("a");
		assertEquals("a", message.getSessionId());
	}

	/**
	 * _shortMaxString_givenStringSessionId
	 */
	@Test
	public void T43_SessionId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCMessage) message).setSessionId(sb.toString());
		assertEquals(sb.toString(), message.getSessionId());
	}
}
