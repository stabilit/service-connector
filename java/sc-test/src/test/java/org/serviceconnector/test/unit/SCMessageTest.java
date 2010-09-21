package org.serviceconnector.test.unit;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.ISCMessage;
import org.serviceconnector.api.SCMessage;


/**
 * @author FJurnecka
 * 
 */

public class SCMessageTest {

	private ISCMessage message;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		message = new SCMessage();
	}

	@Test
	public void construtor_setNoValues_valuesEmpty() {
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
	}

	@Test
	public void constructor_setNullDataParam_valuesEmpty() {
		message = new SCMessage(null);
		assertEquals(null, message.getMessageInfo());
		assertEquals(null, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
		assertEquals(false, message.isFault());
	}

	@Test
	public void constructor_setObjectDataParam_dataContainsObject() {
		Object obj = new Object();
		message = new SCMessage(obj);
		assertEquals(null, message.getMessageInfo());
		assertEquals(obj, message.getData());
		assertEquals(null, message.getSessionId());
		assertEquals(true, message.isCompressed());
		assertEquals(false, message.isFault());
	}

	@Test
	public void setCompressed_setTrue_true() {
		message.setCompressed(true);
		assertEquals(true, message.isCompressed());
	}

	@Test
	public void setCompressed_setFalse_false() {
		message.setCompressed(false);
		assertEquals(false, message.isCompressed());
	}

	@Test
	public void setData_nullParam_nullData() {
		message.setData(null);
		assertEquals(null, message.getData());
	}

	@Test
	public void setData_emptyObject_emptyObject() {
		Object obj = new Object();
		message.setData(obj);
		assertEquals(obj, message.getData());
	}

	@Test
	public void setData_arbitraryString_givenString() {
		message.setData("The quick brown fox jumps over a lazy dog.");
		assertEquals("The quick brown fox jumps over a lazy dog.", message
				.getData());
	}

	@Test
	public void setData_1MBArray_allocated1MBArray() {
		message.setData(new byte[1048576]);
		assertEquals(1048576, ((byte[]) message.getData()).length);
	}

	@Test
	public void setMessageInfo_nullParam_acceptValue() {
		message.setMessageInfo(null);
		assertEquals(null, message.getMessageInfo());
	}

	@Test(expected = InvalidParameterException.class)
	public void setMessageInfo_emptyParam_throwInvalidParamException() {
		message.setMessageInfo("");
	}
	
	@Test
	public void setMessageInfo_whiteCharParam_throwInvalidParamException() {
		message.setMessageInfo(" ");
		assertEquals(" ", message.getMessageInfo());
	}

	@Test
	public void setMessageInfo_sinlgeCharParam_length1MessageInfo() {
		message.setMessageInfo("a");
		assertEquals("a", message.getMessageInfo());
		assertEquals(1, message.getMessageInfo().length());
	}

	@Test
	public void setMessageInfo_256CharParam_length256MessageInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
		assertEquals(sb.toString(), message.getMessageInfo());
		assertEquals(256, message.getMessageInfo().length());
	}

	@Test(expected = InvalidParameterException.class)
	public void setMessageInfo_257CharParam_throwInvalidParameterException() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	@Test(expected = InvalidParameterException.class)
	public void setMessageInfo_shortMaxCharParam_throwInvalidParameterException() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	@Test
	public void setSessionId_nullParam_nullSessionId() {
		((SCMessage) message).setSessionId(null);
		assertEquals(null, message.getSessionId());
	}

	@Test
	public void setSessionId_emptyString_emptySessionId() {
		((SCMessage) message).setSessionId("");
		assertEquals("", message.getSessionId());
	}

	@Test
	public void setSessionId_oneCharString_givenCharSessionId() {
		((SCMessage) message).setSessionId("a");
		assertEquals("a", message.getSessionId());
	}

	@Test
	public void setSessionId_shortMaxString_givenStringSessionId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCMessage) message).setSessionId(sb.toString());
		assertEquals(sb.toString(), message.getSessionId());
	}
}
