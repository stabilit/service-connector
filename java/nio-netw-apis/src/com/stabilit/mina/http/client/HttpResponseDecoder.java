package com.stabilit.mina.http.client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * TODO HttpResponseDecoder.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$, $Date$
 */
public class HttpResponseDecoder extends CumulativeProtocolDecoder {

	public final static String CURRENT_RESPONSE = "CURRENT_RESPONSE";

	private HttpDecoder httpDecoder = new HttpDecoder();

	public boolean doDecode(IoSession ioSession, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		HttpResponseMessage response = (HttpResponseMessage) ioSession
				.getAttribute(CURRENT_RESPONSE);
		if (response == null) {
			response = new HttpResponseMessage();
			ioSession.setAttribute(CURRENT_RESPONSE, response);
		}

		// Test if we need the response...
		if (response.getState() == HttpResponseMessage.STATE_START) {

			if (!processStatus(response, in)) {
				return false;
			}

			// Handle HTTP/1.1 Continue
			if (response.getStatusCode() == 100) {
				response.setState(HttpResponseMessage.STATE_STATUS_CONTINUE);
			} else {
				response.setState(HttpResponseMessage.STATE_STATUS_READ);
			}
		}

		// If we are in a Continue, read until we get the real header
		if (response.getState() == HttpResponseMessage.STATE_STATUS_CONTINUE) {
			// Continue reading until we get a blank line
			while (true) {
				String line = httpDecoder.decodeLine(in);

				// Check if the entire response has been read
				if (line == null)
					return false;

				// Check if the entire response headers have been read
				if (line.length() == 0) {
					response.setState(HttpResponseMessage.STATE_STATUS_READ);

					// The next line should be a header
					if (!processStatus(response, in)) {
						return false;
					}
					break;
				}
			}
		}

		// Are we reading headers?
		if (response.getState() == HttpResponseMessage.STATE_STATUS_READ) {
			if (processHeaders(response, in) == false) {
				return false;
			}
		}

		// Are we reading content?
		if (response.getState() == HttpResponseMessage.STATE_HEADERS_READ) {
			if (processContent(response, in) == false) {
				return false;
			}
		}

		// If we are chunked and we have read all the content, then read the
		// footers if there are any
		if (response.isChunked()
				&& response.getState() == HttpResponseMessage.STATE_CONTENT_READ) {
			if (processFooters(response, in) == false) {
				return false;
			}
		}

		response.setState(HttpResponseMessage.STATE_FINISHED);

		out.write(response);

		ioSession.removeAttribute(CURRENT_RESPONSE);

		return true;
	}

	private boolean processHeaders(HttpResponseMessage response, IoBuffer in)
			throws Exception {
		if (!findHeaders(response, in))
			return false;

		response.setState(HttpResponseMessage.STATE_HEADERS_READ);
		return true;
	}

	private boolean processFooters(HttpResponseMessage response, IoBuffer in)
			throws Exception {
		if (!findHeaders(response, in))
			return false;

		response.setState(HttpResponseMessage.STATE_FOOTERS_READ);
		return true;
	}

	private boolean findHeaders(HttpResponseMessage response, IoBuffer in)
			throws Exception {
		// Read the headers and process them
		while (true) {
			String line = httpDecoder.decodeLine(in);

			// Check if the entire response has been read
			if (line == null)
				return false;

			// Check if the entire response headers have been read
			if (line.length() == 0) {
				break;
			}

			httpDecoder.decodeHeader(line, response);
		}
		return true;
	}

	private boolean processContent(HttpResponseMessage response, IoBuffer in)
			throws Exception {
		if (response.isChunked()) {
			while (true) {
				// Check what kind of record we are reading (content or size)
				if (response.getExpectedToRead() == HttpResponseMessage.EXPECTED_NOT_READ) {
					// We haven't read the size, so we are expecting a size
					String line = httpDecoder.decodeLine(in);

					// Check if the entire line has been read
					if (line == null)
						return false;

					response.setExpectedToRead(httpDecoder.decodeSize(line));

					// Are we done reading the chunked content? (A zero means we
					// are done)
					if (response.getExpectedToRead() == 0) {
						break;
					}
				}

				// Now read the content chunk

				// Be sure all of the data is there for us to retrieve + the
				// CRLF...
				if (response.getExpectedToRead() + 2 > in.remaining()) {
					// Need more data
					return false;
				}

				// Read the content
				httpDecoder.decodeChunkedContent(in, response);

				// Flag that it's time to read a size record
				response
						.setExpectedToRead(HttpResponseMessage.EXPECTED_NOT_READ);

			}

		} else if (response.getContentLength() > 0) {
			// Do we have enough data?
			if ((response.getContentLength()) > in.remaining())
				return false;
			httpDecoder.decodeContent(in, response);
		}

		response.setState(HttpResponseMessage.STATE_CONTENT_READ);

		return true;
	}

	private boolean processStatus(HttpResponseMessage response, IoBuffer in)
			throws Exception {
		// Read the status header
		String header = httpDecoder.decodeLine(in);
		if (header == null)
			return false;

		httpDecoder.decodeStatus(header, response);

		return true;
	}
}