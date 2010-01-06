package com.stabilit.mina.http.client;

import java.net.ProtocolException;
import java.util.Map;
import java.util.HashMap;

/**
 * TODO HttpRequestMessage.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$, $Date$
 */
public class HttpRequestMessage extends HttpMessage {

	public static String REQUEST_GET = "GET";
	public static String REQUEST_POST = "POST";
	public static String REQUEST_HEAD = "HEAD";
	public static String REQUEST_OPTIONS = "OPTIONS";
	public static String REQUEST_PUT = "PUT";
	public static String REQUEST_DELETE = "DELETE";
	public static String REQUEST_TRACE = "TRACE";

	private String requestMethod = REQUEST_GET;
	private String path;
	private Map<String, String> parameters = new HashMap<String, String>();

	public HttpRequestMessage(String path) {
		this.path = path;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) throws ProtocolException {
		if (requestMethod.equals(REQUEST_GET)
				|| requestMethod.equals(REQUEST_POST)
				|| requestMethod.equals(REQUEST_HEAD)
				|| requestMethod.equals(REQUEST_OPTIONS)
				|| requestMethod.equals(REQUEST_PUT)
				|| requestMethod.equals(REQUEST_DELETE)
				|| requestMethod.equals(REQUEST_TRACE)) {
			this.requestMethod = requestMethod;
			return;
		}

		throw new ProtocolException("Invalid request method type.");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path == null || path.trim().length() == 0)
			path = "/";
		this.path = path;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}

	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}
}
