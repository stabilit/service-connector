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
package com.stabilit.sc.common.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import com.stabilit.sc.common.net.nio.NioHttpException;
import com.stabilit.sc.common.scmp.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.IEncoderDecoder;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPBodyType;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPPartID;

public class SCMPStreamHttpUtil {

	private IEncoderDecoder encoderDecoder;

	public SCMPStreamHttpUtil() {
		encoderDecoder = null;
	}

	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.encoderDecoder = encoderDecoder;
	}

	public SCMP readSCMP(InputStream is) throws Exception {
		// read headers
		String line = readLine(is);
		int contentLength = -1;
		while (line != null) {
			if (line.length() <= 0) {
				break;
			}
			// extract name from value
			int pos = line.indexOf(":");
			if (pos > 0) {
				String name = line.substring(0, pos);
				String value = line.substring(pos + 1, line.length());
				if ("content-length".equalsIgnoreCase(name)) {
					contentLength = Integer.parseInt(value.trim());
				}
			}
			line = readLine(is);
		}
		if (contentLength <= 0) {
			return null;
		}
		byte[] stream = new byte[contentLength];
		int bytesRead = 0;
		while (bytesRead < stream.length) {
			int readSize = is.read(stream, bytesRead, contentLength - bytesRead);
			if (readSize < 0) {
				throw new NioHttpException("http stream util read failure  (<0)");
			}
			bytesRead += readSize;
		}
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(stream);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(stream);
		SCMP scmp = (SCMP) encoderDecoder.decode(bais);
		return scmp;
	}

	private String readLine(InputStream in) throws IOException {
		char[] buf = new char[128];
		int room = buf.length;
		int offset = 0;
		int c;

		loop: while (true) {
			switch (c = in.read()) {
			case -1:
			case '\n':
				break loop;
			case '\r':
				int c2 = in.read();
				if ((c2 != '\n') && (c2 != -1)) {
					if (!(in instanceof PushbackInputStream)) {
						in = new PushbackInputStream(in);
					}
					((PushbackInputStream) in).unread(c2);
				}
				break loop;
			default:
				if (--room < 0) {
					char[] newbuf = new char[offset + 128];
					room = newbuf.length - offset - 1;
					System.arraycopy(buf, 0, newbuf, 0, offset);
					buf = newbuf;
				}
				buf[offset++] = (char) c;
				break;
			}
		}
		if ((c == -1) && (offset == 0)) {
			return null;
		}
		return String.copyValueOf(buf, 0, offset);
	}

	public void writeRequestSCMP(OutputStream os, String path, SCMP scmp) throws Exception {
		// POST / HTTP/1.1
		// User-Agent: Java/1.6.0_06
		// Host: 127.0.0.1:88
		// Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
		// Connection: keep-alive
		// Content-type: application/x-www-form-urlencoded
		// Content-Length: 357
		StringBuilder http = new StringBuilder();
		http.append("POST / HTTP/1.1\r\n");
		http.append("User-Agent: ");
		http.append(System.getProperty("java.runtime.version"));
		http.append("\r\n");
		http.append("Host: ");
		http.append(path);
		http.append("\r\n");
		http.append("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n");
		http.append("Connection: keep-alive\r\n");
		http.append("Content-type: ");
		http.append(this.getMimeType(scmp));
		http.append("\r\n");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (this.encoderDecoder == null) {
			encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		}
		encoderDecoder.encode(baos, scmp);
		baos.close();
		byte[] objStream = baos.toByteArray();
		http.append("Content-Length: ");
		http.append(objStream.length);
		http.append("\r\n");
		http.append("\r\n");
		os.write(http.toString().getBytes());
		os.flush();
		os.write(objStream);
		os.flush();
	}

	private String getMimeType(SCMP scmp) {
		SCMPBodyType bodyType = scmp.getBodyType();
		return bodyType.getMimeType();
	}

	public void writeResponseSCMP(OutputStream os, SCMP scmp) throws Exception {
		StringBuilder http = new StringBuilder();
		http.append("HTTP/1.1 200 OK\r\n");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EncoderDecoderFactory encoderDecoderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();
		if (this.encoderDecoder == null) {
			encoderDecoder = encoderDecoderFactory.newInstance(scmp);
		}
		if (encoderDecoderFactory.isLarge(scmp)) {
			if (scmp.getHeader(SCMPHeaderAttributeKey.PART_ID) == null) {
				scmp.setHeader(SCMPHeaderAttributeKey.PART_ID, SCMPPartID.getNextAsString());
			}
		}
		encoderDecoder.encode(baos, scmp);
		baos.close();
		byte[] objStream = baos.toByteArray();
		http.append("Content-Length: ");
		http.append(objStream.length);
		http.append("\r\n");
		http.append("\r\n");
		os.write(http.toString().getBytes());
		os.write(objStream);
		os.flush();
		os.close();
	}

}
