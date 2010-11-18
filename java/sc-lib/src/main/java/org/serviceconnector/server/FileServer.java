package org.serviceconnector.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.FileSession;

public class FileServer extends Server {

	public FileServer(String serverKey, InetSocketAddress socketAddress, String serviceName, int portNr, int maxConnections,
			String connectionType, int keepAliveInterval) {
		super(ServerType.FILE_SERVER, socketAddress, serviceName, portNr, maxConnections, connectionType, keepAliveInterval,
				Constants.OPERATION_TIMEOUT_MULTIPLIER);
		this.serverKey = serverKey;
	}

	public SCMPMessage serverUploadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutMillis)
			throws Exception {
		OutputStream out = null;
		HttpURLConnection httpCon = null;
		
		if (session.isStreaming()) {
			// streaming already started
			httpCon = session.getHttpURLConnection();
			out = httpCon.getOutputStream();
		} else {
			// first stream package arrived - set up URL connection
			String path = session.getPath();
			URL url = new URL("http://" + this.host + ":" + this.portNr + path + Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT_NAME
					+ "?name=" + remoteFileName);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("PUT");
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			// enable streaming of HTTP
			httpCon.setChunkedStreamingMode(2048);
			httpCon.connect();
			out = httpCon.getOutputStream();
			// set session to streaming mode
			session.startStreaming();
			session.setHttpUrlConnection(httpCon);
		}
		try {
			// write the data to the server
			out.write((byte[]) message.getBody());
			out.flush();
		} catch (Exception e) {
			SCMPFault fault = new SCMPFault(e);
			return fault;
		}

		SCMPMessage reply = null;
		if (message.isPart() == false) {
			// last package arrived
			out.close();
			if (httpCon.getResponseCode() != HttpResponseStatus.OK.getCode()) {
				// error handling
				SCMPFault fault = new SCMPFault(SCMPError.UPLOAD_FILE_FAILED, httpCon.getResponseMessage());
				return fault;
			}
			httpCon.disconnect();
			session.stopStreaming();
			reply = new SCMPMessage();
		} else {
			// set up poll request
			reply = new SCMPPart();
		}
		return reply;
	}

	public SCMPMessage serverDownloadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutInSeconds)
			throws Exception {
		InputStream in = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			in = session.getInputStream();
		} else {
			// download request arrived - set up URL connection
			String path = session.getPath();
			try {
				URL url = new URL("http://" + this.host + ":" + this.portNr + path + remoteFileName);
				httpCon = (HttpURLConnection) url.openConnection();
				httpCon.connect();
				in = httpCon.getInputStream();
			} catch (Exception e) {
				SCMPFault fault = new SCMPFault(SCMPError.SERVER_ERROR, httpCon.getResponseMessage() + " " + e.getMessage());
				return fault;
			}
			// set session to streaming mode
			session.startStreaming();
			session.setHttpUrlConnection(httpCon);
			session.setInputStream(in);
		}
		try {
			// write the data to the client
			SCMPMessage reply = null;
			byte[] fullBuffer = new byte[Constants.MAX_MESSAGE_SIZE];
			int readBytes = in.read(fullBuffer);
			if (readBytes < 0) {
				// this is the end
				reply = new SCMPMessage();
				reply.setBody(new byte[0]);
				in.close();
				session.getHttpURLConnection().disconnect();
				session.stopStreaming();
				return reply;
			}
			reply = new SCMPPart();
			reply.setBody(fullBuffer, 0, readBytes);
			return reply;
		} catch (Exception e) {
			SCMPFault fault = new SCMPFault(e);
			return fault;
		}
	}

	public void serverListFiles(int timeoutInSeconds) throws Exception {

	}

	/** {@inheritDoc} */
	@Override
	public void abortSession(AbstractSession session) {
		FileSession fileSession = (FileSession) session;
		HttpURLConnection httpURLConnection = fileSession.getHttpURLConnection();
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
	}
}
