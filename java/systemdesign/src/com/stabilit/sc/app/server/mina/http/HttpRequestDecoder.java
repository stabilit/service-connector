/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.stabilit.sc.app.server.mina.http;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * A {@link org.apache.mina.filter.codec.demux.MessageDecoder} that decodes
 * HttpRequest.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007)
 *          $
 */
public class HttpRequestDecoder extends MessageDecoderAdapter {
	private static final byte[] CONTENT_LENGTH = new String("Content-Length:")
			.getBytes();

	private CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
	private HttpRequestMessage request = null;

	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// Return NEED_DATA if the whole header is not read yet.
		try {
			return messageComplete(in) ? MessageDecoderResult.OK
					: MessageDecoderResult.NEED_DATA;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return MessageDecoderResult.NOT_OK;
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// Try to decode body
		HttpRequestMessage m = decodeBody(in);

		// Return NEED_DATA if the body is not fully read.
		if (m == null)
			return MessageDecoderResult.NEED_DATA;

		out.write(m);

		return MessageDecoderResult.OK;
	}

	private boolean messageComplete(IoBuffer in) throws Exception {
		int last = in.remaining() - 1;
		if (in.remaining() < 4)
			return false;

		// to speed up things we check if the Http request is a GET or POST
		if (in.get(0) == (byte) 'G' && in.get(1) == (byte) 'E'
				&& in.get(2) == (byte) 'T') {
			// Http GET request therefore the last 4 bytes should be 0x0D 0x0A
			// 0x0D 0x0A
			return (in.get(last) == (byte) 0x0A
					&& in.get(last - 1) == (byte) 0x0D
					&& in.get(last - 2) == (byte) 0x0A && in.get(last - 3) == (byte) 0x0D);
		} else if (in.get(0) == (byte) 'P' && in.get(1) == (byte) 'O'
				&& in.get(2) == (byte) 'S' && in.get(3) == (byte) 'T') {
			// Http POST request
			// first the position of the 0x0D 0x0A 0x0D 0x0A bytes
			int eoh = -1;
			for (int i = last; i > 2; i--) {
				if (in.get(i) == (byte) 0x0A && in.get(i - 1) == (byte) 0x0D
						&& in.get(i - 2) == (byte) 0x0A
						&& in.get(i - 3) == (byte) 0x0D) {
					eoh = i + 1;
					break;
				}
			}
			if (eoh == -1)
				return false;
			for (int i = 0; i < last; i++) {
				boolean found = false;
				for (int j = 0; j < CONTENT_LENGTH.length; j++) {
					if (in.get(i + j) != CONTENT_LENGTH[j]) {
						found = false;
						break;
					}
					found = true;
				}
				if (found) {
					// retrieve value from this position till next 0x0D 0x0A
					StringBuilder contentLength = new StringBuilder();
					for (int j = i + CONTENT_LENGTH.length; j < last; j++) {
						if (in.get(j) == 0x0D)
							break;
						contentLength.append(new String(
								new byte[] { in.get(j) }));
					}
					// if content-length worth of data has been received then
					// the message is complete
					return (Integer.parseInt(contentLength.toString().trim())
							+ eoh == in.remaining());
				}
			}
		}

		// the message is not complete and we need more data
		return false;
	}

	private HttpRequestMessage decodeBody(IoBuffer in) {
		request = new HttpRequestMessage();
		try {
			request.setHeaders(parseRequest(in.asInputStream()));
			return request;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private Map parseRequest(InputStream is) {
		Map<String, Object[]> map = new HashMap<String, Object[]>();
		try {
			DataInputStream dis = new DataInputStream(is);
			// Get request URL.
			String line = dis.readLine();
			String[] url = line.split(" ");
			if (url.length < 3)
				return map;

			map.put("URI", new String[] { line });
			map.put("Method", new String[] { url[0].toUpperCase() });
			map.put("Context", new String[] { url[1].substring(1) });
			map.put("Protocol", new String[] { url[2] });
			// Read header
			while ((line = dis.readLine()) != null && line.length() > 0) {
				String[] tokens = line.split(": ");
				map.put(tokens[0], new String[] { tokens[1] });
			}
			int len = Integer.parseInt((String)map.get("Content-Length")[0]);
			byte[] bbuf = new byte[len];
			int readLen = dis.read(bbuf);
			ByteArrayInputStream bais = new ByteArrayInputStream(bbuf);
			ObjectInputStream ois = new ObjectInputStream (bais);
			Object obj = ois.readObject();
			map.put("object", new Object[] {obj});
//			System.out.println(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return map;
	}
}
