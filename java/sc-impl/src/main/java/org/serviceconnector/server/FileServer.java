package org.serviceconnector.server;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import org.serviceconnector.Constants;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;

public class FileServer extends Server {

	HttpURLConnection httpCon = null;
	OutputStream out = null;

	public FileServer(String serverKey, InetSocketAddress socketAddress, String serviceName, int portNr, int maxConnections,
			String connectionType, int keepAliveInterval) {
		super(ServerType.FILE_SERVER, socketAddress, serviceName, portNr, maxConnections, connectionType, keepAliveInterval,
				Constants.OPERATION_TIMEOUT_MULTIPLIER);
		this.serverKey = serverKey;
		try {
			URL url = new URL("http://localhost/sc/scupload.php?name=uploadFile.txt");
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("PUT");
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setChunkedStreamingMode(2048); // enable streaming of HTTP
			httpCon.connect();
			out = httpCon.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void serverUploadFile(SCMPMessage message, String path, byte[] data, ISCMPCallback callback, int timeoutMillis)
			throws Exception {
		out.write(data);
		out.flush();

		SCMPMessage reply = null;
		if (message.isPart() == false) {
			// last package arrived
			out.close();
			reply = new SCMPMessage();
		} else {
			// set up poll request
			reply = new SCMPPart();
		}
		reply.setIsReply(true);
		reply.setMessageType(SCMPMsgType.FILE_UPLOAD);
		callback.callback(reply);
	}

	public void serverDownloadFile(String remoteFileName, OutputStream outStream, int timeoutInSeconds) throws Exception {

	}

	public void serverListFiles(int timeoutInSeconds) throws Exception {

	}
}
