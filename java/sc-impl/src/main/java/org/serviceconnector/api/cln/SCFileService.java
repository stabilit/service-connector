package org.serviceconnector.api.cln;

import java.io.InputStream;
import java.io.OutputStream;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCService;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPFileDownloadCall;
import org.serviceconnector.call.SCMPFileListCall;
import org.serviceconnector.call.SCMPFileUploadCall;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;

public class SCFileService extends SCService {

	public SCFileService(String serviceName, SCContext scContext) {
		super(serviceName, scContext);
		this.requester = new SCRequester(new RequesterContext(scContext.getConnectionPool(), this.msgId));
		this.scServiceContext = new SCServiceContext(this);
	}

	public void uploadFile(String remoteFileName, InputStream inStream, int timeoutInSeconds) throws Exception {
		SCMPFileUploadCall uploadFileCall = (SCMPFileUploadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(this.requester,
				this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);

		uploadFileCall.setRequestBody(inStream);
		uploadFileCall.setRemoteFileName(remoteFileName);
		try {
			uploadFileCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("upload File failed ", e);
		}
		SCMPMessage reply = callback.getMessageSync(timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			throw new SCServiceException("upload File failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	public void downloadFile(String remoteFileName, OutputStream outStream, int timeoutInSeconds) throws Exception {
		SCMPFileDownloadCall downloadFileCall = (SCMPFileDownloadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(this.requester,
				this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);

		downloadFileCall.setRemoteFileName(remoteFileName);
		downloadFileCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);

		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			throw new SCServiceException("upload File failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
		outStream.write((byte[]) reply.getBody());
	}

	public void listFiles(int timeoutInSeconds) throws Exception {
		SCMPFileListCall fileListCall = (SCMPFileListCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(this.requester,
				this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);
		fileListCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			throw new SCServiceException("upload File failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}
}
