/*
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
 */
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
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPLargeResponse;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

public class SCFileService extends SCService {

	public SCFileService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
	}

	public synchronized void uploadFile(String remoteFileName, InputStream inStream) throws Exception {
		this.uploadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, inStream);
	}

	public synchronized void uploadFile(int operationTimeoutSeconds, String remoteFileName, InputStream inStream) throws Exception {
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		try {
			SCMPFileUploadCall uploadFileCall = (SCMPFileUploadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(this.requester,
					this.serviceName, this.sessionId);
			SCServiceCallback callback = new SCServiceCallback(true);

			uploadFileCall.setRequestBody(inStream);
			uploadFileCall.setRemoteFileName(remoteFileName);
			this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
			try {
				uploadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("upload file failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("upload file failed");
				ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
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
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		try {
			SCMPFileDownloadCall downloadFileCall = (SCMPFileDownloadCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(
					this.requester, this.serviceName, this.sessionId);
			SCServiceCallback callback = new SCServiceCallback(true);

			downloadFileCall.setRemoteFileName(remoteFileName);
			this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
			downloadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);

			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("download file failed");
				ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
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

	public synchronized void listFiles(int operationTimeoutSeconds) throws Exception {
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		SCMPFileListCall fileListCall = (SCMPFileListCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(this.requester,
				this.serviceName);
		SCServiceCallback callback = new SCServiceCallback(true);
		fileListCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("upload File failed");
			ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
	}

	private void createFileSession(int operationTimeoutSeconds) throws SCServiceException {
		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		createSessionCall.setEchoIntervalSeconds(Constants.DEFAULT_FILE_SESSION_TIMEOUT_SECONDS);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("create file session failed ", e);
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("create file session failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
		this.sessionId = reply.getSessionId();
	}

	private synchronized void deleteFileSession(int operationTimeoutSeconds) throws Exception {
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			try {
				this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("delete file session failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("delete file session failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
		} finally {
			this.sessionId = null;
		}
	}
}