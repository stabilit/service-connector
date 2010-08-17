package unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;

/**
 * @author FJurnecka
 *
 */

public class SCMessageTest {
	
	private ISCMessage message;

	@Before
	public void setUp() throws Exception {
		message = new SCMessage();
	}
	
	@Test
	public void construtor_setNoValues_valuesEmpty()
	{
		assertEquals(null, message.getMessageInfo());		
	}
}
