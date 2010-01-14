/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */

package com.stabilit.netty.http.server;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A utility class that provides various common operations and constants related
 * with {@link Charset} and its relevant classes.
 * 
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (trustin@gmail.com)
 * @version $Rev: 1$, $Date: 2009-12-16:24:+0(Tue, Dec 2009) $
 */
public class CharsetUtil {

	/**
	 * 16-bit UTF (UCS Transformation Format) whose byte order is identified by
	 * an optional byte-order mark
	 */
	public static final Charset UTF_ = Charset.forName("UTF-16");

	/**
	 * 16-bit UTF (UCS Transformation Format) whose byte order is big-endian
	 */
	public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

	/**
	 * 16-bit UTF (UCS Transformation Format) whose byte order is little-endian
	 */
	public static final Charset UTF_16LE = Charset.forName("UTF-16LE");

	/**
	 * 8-bit UTF (UCS Transformation Format)
	 */
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * ISO Latin Alphabet No. 1, as known as <tt>ISO-LATIN-1</tt>
	 */
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

	/**
	 * 7-bit ASCII, as known as ISO646-US or the Basic Latin block of the
	 * Unicode character set
	 */
	public static final Charset US_ASCII = Charset.forName("US-ASCII");

	private static final ThreadLocal<Map<Charset, CharsetEncoder>> encoders = new ThreadLocal<Map<Charset, CharsetEncoder>>() {
		@Override
		protected Map<Charset, CharsetEncoder> initialValue() {
			return new IdentityHashMap<Charset, CharsetEncoder>();
		}
	};

	private static final ThreadLocal<Map<Charset, CharsetDecoder>> decoders = new ThreadLocal<Map<Charset, CharsetDecoder>>() {
		@Override
		protected Map<Charset, CharsetDecoder> initialValue() {
			return new IdentityHashMap<Charset, CharsetDecoder>();
		}
	};

	/**
	 * Returns a cached thread-local {@link CharsetEncoder} for the specified
	 * <tt>charset</tt>.
	 */
	public static CharsetEncoder getEncoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}

		Map<Charset, CharsetEncoder> map = encoders.get();
		CharsetEncoder e = map.get(charset);
		if (e != null) {
			e.reset();
			e.onMalformedInput(CodingErrorAction.REPLACE);
			e.onUnmappableCharacter(CodingErrorAction.REPLACE);
			return e;
		}

		e = charset.newEncoder();
		e.onMalformedInput(CodingErrorAction.REPLACE);
		e.onUnmappableCharacter(CodingErrorAction.REPLACE);
		map.put(charset, e);
		return e;
	}

	/**
	 * Returns a cached thread-local {@link CharsetDecoder} for the specified
	 * <tt>charset</tt>.
	 */
	public static CharsetDecoder getDecoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}

		Map<Charset, CharsetDecoder> map = decoders.get();
		CharsetDecoder d = map.get(charset);
		if (d != null) {
			d.reset();
			d.onMalformedInput(CodingErrorAction.REPLACE);
			d.onUnmappableCharacter(CodingErrorAction.REPLACE);
			return d;
		}

		d = charset.newDecoder();
		d.onMalformedInput(CodingErrorAction.REPLACE);
		d.onUnmappableCharacter(CodingErrorAction.REPLACE);
		map.put(charset, d);
		return d;
	}

	private CharsetUtil() {
		// Unused
	}
}
