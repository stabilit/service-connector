package org.serviceconnector.api.cln;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCService;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPFileDownloadCall;
import org.serviceconnector.call.SCMPFileListCall;
import org.serviceconnector.call.SCMPFileUploadCall;
import org.serviceconnector.scmp.SCMPMessage;

public class SCFileService extends SCService {

	public SCFileService(String serviceName, SCContext scContext) {
		super(serviceName, scContext);
	}

	public void uploadFile(String remoteFileName, InputStream inStream, int timeoutInSeconds) throws Exception {
		SCMPFileUploadCall uploadFileCall = (SCMPFileUploadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(
				this.requester, this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(inStream.read());
		uploadFileCall.setRequestBody(bos.toByteArray());
		uploadFileCall.setRemoteFileName(remoteFileName);
		uploadFileCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		callback.getMessageSync();
	}

	public void downloadFile(String remoteFileName, OutputStream outStream, int timeoutInSeconds) throws Exception {
		SCMPFileDownloadCall downloadFileCall = (SCMPFileDownloadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(
				this.requester, this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);

		downloadFileCall.setRemoteFileName(remoteFileName);
		downloadFileCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage message = callback.getMessageSync();

		outStream.write((byte[]) message.getBody());
	}

	public void listFiles(int timeoutInSeconds) throws Exception {
		SCMPFileListCall fileListCall = (SCMPFileListCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(
				this.requester, this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);
		fileListCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage message = callback.getMessageSync();
	}
}
