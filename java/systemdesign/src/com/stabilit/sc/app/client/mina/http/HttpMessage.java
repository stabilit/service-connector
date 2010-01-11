package com.stabilit.sc.app.client.mina.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.http.util.NameValuePair;

/**
 * TODO HttpMessage.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$, $Date$
 */
public class HttpMessage {

	public final static String CONTENT_TYPE = "Content-Type";
	public final static String CONTENT_LENGTH = "Content-Length";

	protected List<NameValuePair> headers = new ArrayList<NameValuePair>();
	protected List<Cookie> cookies = new ArrayList<Cookie>();
	protected String contentType;
	protected int contentLength;
	protected ByteArrayOutputStream content;

	public String getStringContent() {
		if (content == null)
			return null;

		return new String(content.toByteArray());
	}

	public byte[] getContent() {
		if (content == null)
			return null;

		return content.toByteArray();
	}

	public void addContent(byte[] content) throws IOException {
		if (this.content == null)
			this.content = new ByteArrayOutputStream();

		this.content.write(content);
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	public List<NameValuePair> getHeaders() {
		return headers;
	}

	public void setHeaders(List<NameValuePair> headers) {
		this.headers = headers;
	}

	public void addHeader(NameValuePair header) {
		headers.add(header);
	}

	public void addHeader(String name, String value) {
		headers.add(new NameValuePair(name, value));
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

}