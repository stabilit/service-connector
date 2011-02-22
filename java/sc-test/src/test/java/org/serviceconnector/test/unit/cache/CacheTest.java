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
package org.serviceconnector.test.unit.cache;

import java.util.Date;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.TimeMillis;

/**
 * The Class CacheTest tests the core cache functionality.
 * 
 * @author ds
 */
public class CacheTest extends CacheSuperUnitTest {

	/**
	 * Description: Simple cache write test. Write a message into the cache using a dummy id and nr and read the message from cache
	 * again, checking if both contents (body) equals. Verify if cache size is 1.<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_simpleCacheWriteTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);

		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		Date expirationDate = DateTimeUtility.getIncrementTimeMillis(new Date(), +TimeMillis.HOUR.getMillis());
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME, DateTimeUtility
				.getDateTimeAsString(expirationDate));
		CacheId msgCacheId = scmpCache.putMessage(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, msgCacheId.getCacheId());
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, msgCacheId.getSequenceNr());
		CacheMessage cacheMessage = scmpCache.getMessage(scmpMessageRead.getCacheId());
		byte[] bufferRead = (byte[]) cacheMessage.getBody();
		String stringRead = new String(bufferRead);
		Assert.assertEquals(stringWrite, stringRead);
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite(msgCacheId.getCacheId());
		int size = cacheComposite.getSize();
		Assert.assertEquals(1, size);
	}

	/**
	 * Description: Simple cache write test without expiration datetime Write a message into the cache using a dummy id and nr but
	 * without expiration date time<br>
	 * The message won't be written to the cache and an exception is thrown Expectation: passes
	 */
	@Test
	public void t02_noCedCacheWriteTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);

		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		try {
			@SuppressWarnings("unused")
			CacheId msgCacheId = scmpCache.putMessage(scmpMessageWrite);
			Assert.fail("put message should fail, but did not");
		} catch (Exception e) {
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		Assert.assertNull(cacheComposite);
	}

	/**
	 * Description: Simple cache write test into two separate cache instances (Cache). Write the same message into two cache
	 * instances using dummy nr and id. Read both messages from its cache instance and check for equality.<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_duplicateCacheWriteTest() throws CacheException {
		Cache scmpCache1 = this.cacheManager.getCache("dummy1");
		Cache scmpCache2 = this.cacheManager.getCache("dummy2");
		String stringWrite = "this is the buffer";
		byte[] buffer = stringWrite.getBytes();
		SCMPMessage scmpMessageWrite = new SCMPMessage(buffer);

		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		Date expirationDate = DateTimeUtility.getIncrementTimeMillis(new Date(), +TimeMillis.HOUR.getMillis());
		scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME, DateTimeUtility
				.getDateTimeAsString(expirationDate));
		CacheId msgCacheId1 = scmpCache1.putMessage(scmpMessageWrite);
		CacheId msgCacheId2 = scmpCache2.putMessage(scmpMessageWrite);
		SCMPMessage scmpMessageRead = new SCMPMessage();
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, 1233);
		scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
		CacheMessage cacheMessage1 = scmpCache1.getMessage(msgCacheId1);
		byte[] bufferRead1 = (byte[]) cacheMessage1.getBody();
		String stringRead1 = new String(bufferRead1);
		Assert.assertEquals(stringWrite, stringRead1);
		CacheMessage cacheMessage2 = scmpCache2.getMessage(msgCacheId2);
		byte[] bufferRead2 = (byte[]) cacheMessage2.getBody();
		String stringRead2 = new String(bufferRead2);
		Assert.assertEquals(stringWrite, stringRead2);
	}

	/**
	 * Description: Large message (parts) cache write test. Write 10 part messages into the cache using a dummy cache id and nr's.
	 * All messages belong to the same cache id building a tree. Inside the cache a composite node is created and 10 message
	 * instances were assigned to this composite node. This test reads the composite and tries to get all assigned part messages.
	 * Each part message will be identified by a concatenated key using format CACHE_ID/SEQUENCE NR. All messages bodies were tested
	 * for equality.<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_partSCMPCacheWriteTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		Date expirationDate = DateTimeUtility.getIncrementTimeMillis(new Date(), +TimeMillis.HOUR.getMillis());
		String expirationDateTimeString = DateTimeUtility.getDateTimeAsString(expirationDate);
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = null;
			if (i < 10) {
				scmpMessageWrite = new SCMPPart();
			} else {
				scmpMessageWrite = new SCMPMessage();
			}
			scmpMessageWrite.setBody(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME, expirationDateTimeString);
			scmpCache.putMessage(scmpMessageWrite);
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		int size = cacheComposite.getSize();
		Assert.assertEquals(10, size);
		for (int i = 1; i <= 11; i++) {
			String partWrite = stringWrite + i;
			SCMPMessage scmpMessageRead = new SCMPMessage();
			scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, i);
			CacheMessage cacheMessage = scmpCache.getMessage(scmpMessageRead.getFullCacheId());			
			if (cacheMessage == null) {
				if (i < 11) {
					Assert.fail("cacheMessage is null but should not");
					continue;
				}
				break;
			}
			// check if cache id is valid
			boolean validCacheId = cacheComposite.isValidCacheId(scmpMessageRead.getFullCacheId());
			Assert.assertEquals(true, validCacheId);
			byte[] bufferRead = (byte[]) cacheMessage.getBody();
			String stringRead = new String(bufferRead);
			Assert.assertEquals(partWrite, stringRead);
		}
	}

	/**
	 * Description: Huge message (parts) cache write test.<br>
	 * Expectation: passes
	 * 
	 * @see CacheTest#testPartSCMPCacheWrite()
	 */
	@Test
	public void t11_largePartSCMPCacheWriteTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		Date expirationDate = DateTimeUtility.getIncrementTimeMillis(new Date(), +TimeMillis.HOUR.getMillis());
		String expirationDateTimeString = DateTimeUtility.getDateTimeAsString(expirationDate);
		for (int i = 1; i <= 10000; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = null;
			if (i < 10000) {
				scmpMessageWrite = new SCMPPart();
			} else {
				scmpMessageWrite = new SCMPMessage();
			}
			scmpMessageWrite.setBody(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME, expirationDateTimeString);
			scmpCache.putMessage(scmpMessageWrite);
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		int size = cacheComposite.getSize();
		Assert.assertEquals(10000, size);
		for (int i = 1; i <= 10001; i++) {
			String partWrite = stringWrite + i;
			SCMPMessage scmpMessageRead = new SCMPMessage();
			scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpMessageRead.setHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER, i);
			CacheMessage cacheMessage = scmpCache.getMessage(scmpMessageRead.getFullCacheId());
			if (cacheMessage == null) {
				if (i < 10001) {
					Assert.fail("cacheMessage is null but should not");
					continue;
				}
				break;
			}
			byte[] bufferRead = (byte[]) cacheMessage.getBody();
			String stringRead = new String(bufferRead);
			Assert.assertEquals(partWrite, stringRead);
		}
	}

	/**
	 * Description: Large message (parts) cache write test using iterator.<br>
	 * Expectation: passes
	 * 
	 * @see CacheTest#testPartSCMPCacheWrite()
	 */
	@Test
	public void t12_partSCMPCacheWriteUsingIteratorTest() throws CacheException {
		Cache scmpCache = this.cacheManager.getCache("dummy");
		String stringWrite = "this is the part buffer nr = ";
		Date expirationDate = DateTimeUtility.getIncrementTimeMillis(new Date(), +TimeMillis.HOUR.getMillis());
		String expirationDateTimeString = DateTimeUtility.getDateTimeAsString(expirationDate);
		for (int i = 1; i <= 10; i++) {
			String partWrite = stringWrite + i;
			byte[] buffer = partWrite.getBytes();
			SCMPMessage scmpMessageWrite = null;
			if (i < 10) {
				scmpMessageWrite = new SCMPPart();
			} else {
				scmpMessageWrite = new SCMPMessage();
			}
			scmpMessageWrite.setBody(buffer);
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, String.valueOf(1233 + i));
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_ID, "dummy.cache.id");
			scmpMessageWrite.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME, expirationDateTimeString);
			scmpCache.putMessage(scmpMessageWrite);
		}
		// get composite cache of given id
		CacheComposite cacheComposite = scmpCache.getComposite("dummy.cache.id");
		int size = cacheComposite.getSize();
		Assert.assertEquals(10, size);
		Iterator<CacheMessage> cacheIterator = scmpCache.iterator("dummy.cache.id");
		int index = 0;
		while (cacheIterator.hasNext()) {
			index++;
			String partWrite = stringWrite + index;
			@SuppressWarnings("unused")
			byte[] buffer = partWrite.getBytes();
			CacheMessage cacheMessage = cacheIterator.next();
			if (cacheMessage == null) {
				if (index < 11) {
					Assert.fail("cacheMessage is null but should not");
					continue;
				}
				break;
			}
			byte[] bufferRead = (byte[]) cacheMessage.getBody();
			String stringRead = new String(bufferRead);
			Assert.assertEquals(partWrite, stringRead);
		}
	}

}
