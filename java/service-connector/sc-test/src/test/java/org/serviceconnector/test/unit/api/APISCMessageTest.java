/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.unit.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.test.unit.SuperUnitTest;

import junit.framework.Assert;

/**
 * @author FJurnecka
 */
public class APISCMessageTest extends SuperUnitTest {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(APISCMessageTest.class);

	private SCMessage message;

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		message = new SCMessage();
	}

	@Override
	@After
	public void afterOneTest() {
		message = null;
		super.afterOneTest();
	}

	/**
	 * Description: Check default values <br>
	 * Expectation: passed, all values are default
	 */
	@Test
	public void t01_constructor() {
		Assert.assertEquals("data is not null", null, message.getData());
		Assert.assertEquals("data length is not 0", 0, message.getDataLength());
		Assert.assertEquals("messageInfo is not null", null, message.getMessageInfo());
		Assert.assertEquals("sessionInfo is not null", null, message.getSessionInfo());
		Assert.assertEquals("sessionId is not null", null, message.getSessionId());
		Assert.assertEquals("cacheId is not null", null, message.getCacheId());
		Assert.assertEquals("appErrorText is not null", null, message.getAppErrorText());
		Assert.assertEquals("appErrorCode is not empty", Constants.EMPTY_APP_ERROR_CODE, message.getAppErrorCode());
		Assert.assertEquals("compressed flag is not default", Constants.DEFAULT_COMPRESSION_FLAG, message.isCompressed());
		Assert.assertEquals("reject flag is not false", false, message.isReject());
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
		message.setData(new byte[TestConstants.dataLength1MB]);
		Assert.assertEquals("DataParameter ", TestConstants.dataLength1MB, ((byte[]) message.getData()).length);
	}

	/**
	 * Description: Set null value as MessageInfo<br>
	 * Expectation: MessageInfo is set to Null
	 */
	@Test
	public void t30_MessageInfo() throws Exception {
		message.setMessageInfo(null);
		Assert.assertEquals(null, message.getMessageInfo());
	}

	/**
	 * Description: Set single Char as MessageInfo<br>
	 * Expectation: MessageInfo is set to a single char Char
	 */
	@Test
	public void t32_MessageInfo() throws Exception {
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
		message.setMessageInfo(TestConstants.stringLength256);
		Assert.assertEquals(TestConstants.stringLength256, message.getMessageInfo());
		Assert.assertEquals(256, message.getMessageInfo().length());
	}

	/**
	 * Description: Set null value as session Id<br>
	 * Expectation: SessionId is set to Null
	 */
	@Test
	public void t40_SessionId() {
		message.setSessionId(null);
		Assert.assertEquals(null, message.getSessionId());
	}

	/**
	 * Description: Set empty String as session Id<br>
	 * Expectation: SessionId is set to empty String
	 */
	@Test
	public void t41_SessionId() {
		message.setSessionId("");
		Assert.assertEquals("", message.getSessionId());
	}

	/**
	 * Description: Set single char as session Id<br>
	 * Expectation: SessionId is set to single char.
	 */
	@Test
	public void t42_SessionId() {
		message.setSessionId("a");
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
		message.setSessionId(sb.toString());
		Assert.assertEquals(sb.toString(), message.getSessionId());
	}

	/**
	 * Description: Set null value as SessionInfo<br>
	 * Expectation: SessionInfo is set to Null
	 */
	@Test
	public void t50_SessionInfo() throws Exception {
		message.setSessionInfo(null);
		Assert.assertEquals("is not null", null, message.getSessionInfo());
	}

	/**
	 * Description: Set single Char as SessionInfo<br>
	 * Expectation: MessageInfo is set to a single char Char
	 */
	@Test
	public void t52_SessionInfo() throws Exception {
		message.setSessionInfo("a");
		Assert.assertEquals("a", message.getSessionInfo());
		Assert.assertEquals(1, message.getSessionInfo().length());
	}

	/**
	 * Description: Set 256 Chars as SessionInfo<br>
	 * Expectation: MessageInfo is set to a single 256 chars
	 */
	@Test
	public void t53_SessionInfo() throws Exception {
		message.setSessionInfo(TestConstants.stringLength256);
		Assert.assertEquals(TestConstants.stringLength256, message.getSessionInfo());
		Assert.assertEquals(256, message.getSessionInfo().length());
	}

	/**
	 * Description: Set null value as CacheId<br>
	 * Expectation: CacheId is set to Null
	 */
	@Test
	public void t60_CacheId() throws Exception {
		message.setCacheId(null);
		Assert.assertEquals("is not null", null, message.getCacheId());
	}

	/**
	 * Description: Set single Char as CacheId<br>
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
		message.setCacheId(TestConstants.stringLength256);
		Assert.assertEquals(TestConstants.stringLength256, message.getCacheId());
		Assert.assertEquals(256, message.getCacheId().length());
	}

	/**
	 * Description: Set null value as AppErrorText<br>
	 * Expectation: passes
	 */
	@Test
	public void t70_AppErrorText() throws Exception {
		message.setAppErrorText(null);
	}

	/**
	 * Description: Set empty value as AppErrorText<br>
	 * Expectation: AppErrorText is empty
	 */
	@Test
	public void t71_AppErrorText() throws Exception {
		message.setAppErrorText("");
		Assert.assertEquals("is not empty", "", message.getAppErrorText());
	}

	/**
	 * Description: Set blank as AppErrorText<br>
	 * Expectation: AppErrorText is blank
	 */
	@Test
	public void t72_AppErrorText() throws Exception {
		message.setAppErrorText(" ");
		Assert.assertEquals("is not blank", " ", message.getAppErrorText());
	}

	/**
	 * Description: Set single Char as AppErrorText<br>
	 * Expectation: AppErrorText is set to a single char Char
	 */
	@Test
	public void t73_AppErrorText() throws Exception {
		message.setAppErrorText("a");
		Assert.assertEquals("string is not equal", "a", message.getAppErrorText());
		Assert.assertEquals("length is not 1", 1, message.getAppErrorText().length());
	}

	/**
	 * Description: Set 256 Chars as AppErrorText<br>
	 * Expectation: AppErrorText is set to a single 256 chars
	 */
	@Test
	public void t74_AppErrorText() throws Exception {
		message.setAppErrorText(TestConstants.stringLength256);
		Assert.assertEquals("string is not equal", TestConstants.stringLength256, message.getAppErrorText());
		Assert.assertEquals("length is not 256", 256, message.getAppErrorText().length());
	}

	/**
	 * Description: Set 257 Chars as AppErrorText<br>
	 * Expectation: passes, is checked in SC
	 */
	@Test
	public void t75_AppErrorText() throws Exception {
		message.setAppErrorText(TestConstants.stringLength257);
		Assert.assertEquals("length is not 257", 257, message.getAppErrorText().length());
	}

	/**
	 * Description: Set 32767 Chars as AppErrorText<br>
	 * Expectation: passes, is checked in SC
	 */
	@Test
	public void t76_AppErrorText() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			sb.append('a');
		}
		message.setAppErrorText(sb.toString());
		Assert.assertEquals("length is not 32767", Short.MAX_VALUE, message.getAppErrorText().length());
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
	 * Description: Set AppErrorCode -2<br>
	 * Expectation: passes, is checked in SC
	 */
	@Test
	public void t81_AppErrorCode() throws Exception {
		message.setAppErrorCode(-2);
		Assert.assertEquals("is not -2", -2, message.getAppErrorCode());
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
	 * Description: Set new Date as CacheExpirationDateTime<br>
	 * Expectation: passes
	 */
	public void t90_CacheExpirationDateTime() throws Exception {
		message.setCacheExpirationDateTime(new Date());
	}

	/**
	 * Description: Set null as CacheExpirationDateTime<br>
	 * Expectation: passes
	 */
	public void t91_CacheExpirationDateTime() throws Exception {
		message.setCacheExpirationDateTime(null);
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

	/**
	 * Description: Set cached state to true<br>
	 * Expectation: cached flag is set to true
	 */
	@Test
	public void t102_Cached() throws Exception {
		message.setCached(true);
		Assert.assertEquals("Cached", true, message.isCached());
	}

	/**
	 * Description: Set cached state to false<br>
	 * Expectation: cached flag is set to false
	 */
	@Test
	public void t103_Cached() throws Exception {
		message.setCached(false);
		Assert.assertEquals("Cached", false, message.isCached());
	}
}
