/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.test.unit.api;

import java.security.InvalidParameterException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.Loggers;

/**
 * @author FJurnecka
 */
public class APISCMessageTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(APISCMessageTest.class);

	private SCMessage message;

	@Before
	public void beforeOneTest() {
		message = new SCMessage();
	}

	@After
	public void afterOneTest() {
		message = null;
	}

	/**
	 * Description: Check default values <br>
	 * Expectation: passed, all values are default
	 */
	@Test
	public void t01_constructor() {
		Assert.assertEquals("messageInfo is not null", null, message.getMessageInfo());
		Assert.assertEquals("data is not null", null, message.getData());
		Assert.assertEquals("sessionId is not null", null, message.getSessionId());
		Assert.assertEquals("compressed flag is not default", Constants.DEFAULT_COMPRESSION_FLAG, message.isCompressed());
	}

	/**
	 * Description: Set compress modus to true<br>
	 * Expectation: Compress is set to true
	 */
	@Test
	public void t10_Compressed() {
		message.setCompressed(true);
		Assert.assertEquals("Compressed", true, message.isCompressed());
	}

	/**
	 * Description: Set compress modus to false<br>
	 * Expectation: Compress is set to false
	 */
	@Test
	public void t11_Compressed() {
		message.setCompressed(false);
		Assert.assertEquals("Compressed", false, message.isCompressed());
	}

	/**
	 * Description: Create empty message with Object-DataParameter<br>
	 * Expectation: InvalidParameter Exception
	 */
	@Test(expected = InvalidParameterException.class)
	public void t20_Data() {
		Object obj = new Object();
		message.setData(obj);
	}

	/**
	 * Description: Set arbitrary string to DataParameter<br>
	 * Expectation: DataParameter is set to arbitrary string
	 */
	@Test
	public void t21_Data() {
		message.setData(TestConstants.pangram);
		Assert.assertEquals("DataParameter ", TestConstants.pangram, message.getData());
	}

	/**
	 * Description: Set 1MB Array to DataParameter<br>
	 * Expectation: DataParameter is set to 1MB Array
	 */
	@Test
	public void t22_Data() {
		message.setData(new byte[1048576]);
		Assert.assertEquals("DataParameter ", 1048576, ((byte[]) message.getData()).length);
	}

	/**
	 * Description: Set Null-Value as MessageInfo<br>
	 * Expectation: MessageInfo is set to Null
	 */
	@Test
	public void t30_MessageInfo() throws Exception {
		message.setMessageInfo(null);
		Assert.assertEquals(null, message.getMessageInfo());
	}

	/**
	 * Description: Set Empty-Value as MessageInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t31_MessageInfo() throws Exception {
		message.setMessageInfo("");
	}

	/**
	 * Description: Set empty Char as MessageInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t32_MessageInfo() throws Exception {
		message.setMessageInfo(" ");
		Assert.assertEquals(" ", message.getMessageInfo());
	}

	/**
	 * Description: Set sinlge Char as MessageInfo<br>
	 * Expectation: MessageInfo is set to a single char Char
	 */
	@Test
	public void t33_MessageInfo() throws Exception {
		message.setMessageInfo("a");
		Assert.assertEquals("a", message.getMessageInfo());
		Assert.assertEquals(1, message.getMessageInfo().length());
	}

	/**
	 * Description: Set 256 Chars as MessageInfo<br>
	 * Expectation: MessageInfo is set to a single 256 chars
	 */
	@Test
	public void t34_MessageInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
		Assert.assertEquals(sb.toString(), message.getMessageInfo());
		Assert.assertEquals(256, message.getMessageInfo().length());
	}

	/**
	 * Description: Set 257 Chars as MessageInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t35_MessageInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	/**
	 * Description: Set 32767 Chars as MessageInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t36_MessageInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setMessageInfo(sb.toString());
	}

	/**
	 * Description: Set Null-Value as session Id<br>
	 * Expectation: SessionId is set to Null
	 */
	@Test
	public void t40_SessionId() {
		((SCMessage) message).setSessionId(null);
		Assert.assertEquals(null, message.getSessionId());
	}

	/**
	 * Description: Set empty String as session Id<br>
	 * Expectation: SessionId is set to empty String
	 */
	@Test
	public void t41_SessionId() {
		((SCMessage) message).setSessionId("");
		Assert.assertEquals("", message.getSessionId());
	}

	/**
	 * Description: Set single char as session Id<br>
	 * Expectation: SessionId is set to single char.
	 */
	@Test
	public void t42_SessionId() {
		((SCMessage) message).setSessionId("a");
		Assert.assertEquals("a", message.getSessionId());
	}

	/**
	 * Description: Set 32767 Chars as SessionId<br>
	 * Expectation: The sessionId is set to 2767 Chars
	 */
	@Test
	public void t43_SessionId() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		((SCMessage) message).setSessionId(sb.toString());
		Assert.assertEquals(sb.toString(), message.getSessionId());
	}

	/**
	 * Description: Set Null-Value as SessionInfo<br>
	 * Expectation: SessionInfo is set to Null
	 */
	@Test
	public void t50_SessionInfo() throws Exception {
		message.setSessionInfo(null);
		Assert.assertEquals("is not null", null, message.getSessionInfo());
	}

	/**
	 * Description: Set Empty-Value as SessionInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t51_SessionInfo() throws Exception {
		message.setSessionInfo("");
	}

	/**
	 * Description: Set empty Char as SessionInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t52_SessionInfo() throws Exception {
		message.setSessionInfo(" ");
		Assert.assertEquals(" ", message.getSessionInfo());
	}

	/**
	 * Description: Set sinlge Char as SessionInfo<br>
	 * Expectation: MessageInfo is set to a single char Char
	 */
	@Test
	public void t53_SessionInfo() throws Exception {
		message.setSessionInfo("a");
		Assert.assertEquals("a", message.getSessionInfo());
		Assert.assertEquals(1, message.getSessionInfo().length());
	}

	/**
	 * Description: Set 256 Chars as SessionInfo<br>
	 * Expectation: MessageInfo is set to a single 256 chars
	 */
	@Test
	public void t54_SessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setSessionInfo(sb.toString());
		Assert.assertEquals(sb.toString(), message.getSessionInfo());
		Assert.assertEquals(256, message.getSessionInfo().length());
	}

	/**
	 * Description: Set 257 Chars as SessionInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t55_SessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setSessionInfo(sb.toString());
	}

	/**
	 * Description: Set 32767 Chars as SessionInfo<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t56_SessionInfo() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setSessionInfo(sb.toString());
	}

	/**
	 * Description: Set Null-Value as CacheId<br>
	 * Expectation: CacheId is set to Null
	 */
	@Test
	public void t60_CacheId() throws Exception {
		message.setCacheId(null);
		Assert.assertEquals("is not null", null, message.getCacheId());
	}

	/**
	 * Description: Set Empty-Value as CacheId<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t61_CacheId() throws Exception {
		message.setCacheId("");
	}

	/**
	 * Description: Set empty Char as CacheId<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t62_CacheId() throws Exception {
		message.setCacheId(" ");
		Assert.assertEquals(" ", message.getCacheId());
	}

	/**
	 * Description: Set sinlge Char as CacheId<br>
	 * Expectation: MessageInfo is set to a single char Char
	 */
	@Test
	public void t63_CacheId() throws Exception {
		message.setCacheId("a");
		Assert.assertEquals("a", message.getCacheId());
		Assert.assertEquals(1, message.getCacheId().length());
	}

	/**
	 * Description: Set 256 Chars as CacheId<br>
	 * Expectation: MessageInfo is set to a single 256 chars
	 */
	@Test
	public void t64_CacheId() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setCacheId(sb.toString());
		Assert.assertEquals(sb.toString(), message.getCacheId());
		Assert.assertEquals(256, message.getCacheId().length());
	}

	/**
	 * Description: Set 257 Chars as CacheId<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t65_CacheId() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setCacheId(sb.toString());
	}

	/**
	 * Description: Set 32767 Chars as CacheId<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t66_CacheId() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setCacheId(sb.toString());
	}

	/**
	 * Description: Set Null-Value as AppErrorText<br>
	 * Expectation: AppErrorText is set to Null
	 */
	@Test
	public void t70_AppErrorText() throws Exception {
		message.setAppErrorText(null);
		Assert.assertEquals("is not null", null, message.getAppErrorText());
	}

	/**
	 * Description: Set Empty-Value as AppErrorText<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t71_AppErrorText() throws Exception {
		message.setAppErrorText("");
	}

	/**
	 * Description: Set empty Char as AppErrorText<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t72_AppErrorText() throws Exception {
		message.setAppErrorText(" ");
		Assert.assertEquals(" ", message.getAppErrorText());
	}

	/**
	 * Description: Set sinlge Char as AppErrorText<br>
	 * Expectation: AppErrorText is set to a single char Char
	 */
	@Test
	public void t73_AppErrorText() throws Exception {
		message.setAppErrorText("a");
		Assert.assertEquals("a", message.getAppErrorText());
		Assert.assertEquals(1, message.getAppErrorText().length());
	}

	/**
	 * Description: Set 256 Chars as AppErrorText<br>
	 * Expectation: AppErrorText is set to a single 256 chars
	 */
	@Test
	public void t74_AppErrorText() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setAppErrorText(sb.toString());
		Assert.assertEquals(sb.toString(), message.getAppErrorText());
		Assert.assertEquals(256, message.getAppErrorText().length());
	}

	/**
	 * Description: Set 257 Chars as AppErrorText<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t75_AppErrorText() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setAppErrorText(sb.toString());
	}

	/**
	 * Description: Set 32767 Chars as AppErrorText<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t76_AppErrorText() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setAppErrorText(sb.toString());
	}

	/**
	 * Description: Set AppErrorCode 100<br>
	 * Expectation: AppErrorCode is 100
	 */
	@Test
	public void t80_AppErrorCode() throws Exception {
		message.setAppErrorCode(100);
		Assert.assertEquals("is not 100", 100, message.getAppErrorCode());
	}

	/**
	 * Description: Set AppErrorCode -1<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t81_AppErrorCode() throws Exception {
		message.setAppErrorCode(-1);
	}
	
	/**
	 * Description: Set AppErrorCode 0<br>
	 * Expectation: AppErrorCode is 0
	 */
	@Test
	public void t82_AppErrorCode() throws Exception {
		message.setAppErrorCode(0);
		Assert.assertEquals("is not 0", 0, message.getAppErrorCode());
	}

	/**
	 * Description: Set Empty-Value as CacheExpirationDateTime<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t90_CacheExpirationDateTime() throws Exception {
		message.setCacheExpirationDateTime("");
	}

	/**
	 * Description: Set empty Char as CacheExpirationDateTime<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t91_CacheExpirationDateTime() throws Exception {
		message.setCacheExpirationDateTime(" ");
	}

	/**
	 * Description: Set single Char as CacheExpirationDateTime<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t92_CacheExpirationDateTime() throws Exception {
		message.setCacheExpirationDateTime("a");
	}

	/**
	 * Description: Set 256 Chars as CacheExpirationDateTime<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t93_CacheExpirationDateTime() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 256; i++) {
			sb.append('a');
		}
		message.setCacheExpirationDateTime(sb.toString());
	}

	/**
	 * Description: Set 257 Chars as CacheExpirationDateTime<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t94_CacheExpirationDateTime() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}
		message.setCacheExpirationDateTime(sb.toString());
	}

	/**
	 * Description: Set 32767 Chars as CacheExpirationDateTime<br>
	 * Expectation: SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t95_CacheExpirationDateTime() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setCacheExpirationDateTime(sb.toString());
	}
	
	/**
	 * Description: Set reject true<br>
	 * Expectation: reject is set to true
	 */
	@Test
	public void t100_Reject() {
		message.setReject(true);
		Assert.assertEquals("Reject", true, message.isReject());
	}

	/**
	 * Description: Set reject modus to false<br>
	 * Expectation: reject is set to false
	 */
	@Test
	public void t101_Reject() {
		message.setReject(false);
		Assert.assertEquals("Reject", false, message.isReject());
	}

}
