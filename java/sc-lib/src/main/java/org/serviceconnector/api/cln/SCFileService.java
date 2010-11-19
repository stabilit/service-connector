package org.serviceconnector.api.cln;

import java.io.InputStream;
import java.io.OutputStream;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCService;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPFileDownloadCall;
import org.serviceconnector.call.SCMPFileListCall;
import org.serviceconnector.call.SCMPFileUploadCall;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPLargeResponse;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;

public class SCFileService extends SCService {

	public SCFileService(String serviceName, SCContext scContext) {
		super(serviceName, scContext);
		this.requester = new SCRequester(new RequesterContext(scContext.getConnectionPool(), this.msgSequenceNr));
		this.scServiceContext = new SCServiceContext(this);
	}

	public synchronized void uploadFile(String remoteFileName, InputStream inStream) throws Exception {
		this.uploadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, inStream);
	}

	public synchronized void uploadFile(int operationTimeoutSeconds, String remoteFileName, InputStream inStream) throws Exception {
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		try {
			SCMPFileUploadCall uploadFileCall = (SCMPFileUploadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(this.requester,
					this.serviceName, this.sessionId);
			SCServiceCallback callback = new SCServiceCallback(true);

			uploadFileCall.setRequestBody(inStream);
			uploadFileCall.setRemoteFileName(remoteFileName);
			try {
				uploadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("upload file failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				throw new SCServiceException("upload file failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			// always delete file session
			this.deleteFileSession(operationTimeoutSeconds);
		}
	}

	public synchronized void downloadFile(String remoteFileName, OutputStream outStream) throws Exception {
		this.downloadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, outStream);
	}

	public synchronized void downloadFile(int operationTimeoutSeconds, String remoteFileName, OutputStream outStream)
			throws Exception {
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		try {
			SCMPFileDownloadCall downloadFileCall = (SCMPFileDownloadCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(
					this.requester, this.serviceName, this.sessionId);
			SCServiceCallback callback = new SCServiceCallback(true);

			downloadFileCall.setRemoteFileName(remoteFileName);
			downloadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);

			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				throw new SCServiceException("download file failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
			if (reply.isComposite()) {
				((SCMPLargeResponse) reply).getBodyAsStream(outStream);
				return;
			}
			outStream.write((byte[]) reply.getBody());
		} finally {
			// always delete file session
			this.deleteFileSession(operationTimeoutSeconds);
		}
	}

	public synchronized void listFiles(int timeoutInSeconds) throws Exception {
		SCMPFileListCall fileListCall = (SCMPFileListCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(this.requester,
				this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);
		fileListCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage reply = callback.getMessageSync(timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			throw new SCServiceException("upload File failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	private void createFileSession(int timeoutInSeconds) throws SCServiceException {
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		createSessionCall.setEchoIntervalSeconds(Constants.DEFAULT_FILE_SESSION_TIMEOUT_SECONDS);
		try {
			createSessionCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("create file session failed ", e);
		}
		SCMPMessage reply = callback.getMessageSync(timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("create file session failed"
					+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		this.sessionId = reply.getSessionId();
	}

	public synchronized void deleteFileSession(int timeoutInSeconds) throws Exception {
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			try {
				deleteSessionCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("delete file session failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				throw new SCServiceException("delete file session failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			this.sessionId = null;
		}
	}
}