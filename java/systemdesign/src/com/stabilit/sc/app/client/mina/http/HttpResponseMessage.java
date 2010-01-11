package com.stabilit.sc.app.client.mina.http;

/**
 * TODO HttpResponseMessage.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$, $Date$
 */
public class HttpResponseMessage extends HttpMessage {

	static final int EXPECTED_NOT_READ = -1;

	static final int STATE_START = 0;
	static final int STATE_STATUS_CONTINUE = 1;
	static final int STATE_STATUS_READ = 2;
	static final int STATE_HEADERS_READ = 3;
	static final int STATE_CONTENT_READ = 4;
	static final int STATE_FOOTERS_READ = 5;
	static final int STATE_FINISHED = 6;

	private int statusCode;
	private String statusMessage;

	private boolean chunked = false;
	private int expectedToRead = -1;
	private int state = STATE_START;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	boolean isChunked() {
		return chunked;
	}

	void setChunked(boolean chunked) {
		this.chunked = chunked;
	}

	int getExpectedToRead() {
		return expectedToRead;
	}

	void setExpectedToRead(int expectedToRead) {
		this.expectedToRead = expectedToRead;
	}

	int getState() {
		return state;
	}

	void setState(int state) {
		this.state = state;
	}

}