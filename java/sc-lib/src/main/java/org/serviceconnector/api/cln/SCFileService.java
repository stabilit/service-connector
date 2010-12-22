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
import java.util.Arrays;
import java.util.List;

import org.serviceconnector.Constants;
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
		// 1. checking preconditions and initialize
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPFileUploadCall uploadFileCall = (SCMPFileUploadCall) SCMPCallFactory.FILE_UPLOAD_CALL.newInstance(this.requester,
					this.serviceName, this.sessionId);
			uploadFileCall.setRequestBody(inStream);
			uploadFileCall.setRemoteFileName(remoteFileName);
			try {
				uploadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("upload file failed ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("upload file failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			// always delete file session
			this.deleteFileSession(operationTimeoutSeconds);
		}
	}

	public synchronized void downloadFile(String remoteFileName, OutputStream outStream) throws Exception {
		this.downloadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, outStream);
	}

	public synchronized void downloadFile(int operationTimeoutSeconds, String remoteFileName, OutputStream outStream)
			throws Exception {
		// 1. checking preconditions and initialize
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		// first create file session
		this.createFileSession(operationTimeoutSeconds);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPFileDownloadCall downloadFileCall = (SCMPFileDownloadCall) SCMPCallFactory.FILE_DOWNLOAD_CALL.newInstance(
					this.requester, this.serviceName, this.sessionId);
			downloadFileCall.setRemoteFileName(remoteFileName);
			downloadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("download file failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			// 4. post process, reply to client
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

	public synchronized List<String> listFiles() throws Exception {
		return this.listFiles(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	public synchronized List<String> listFiles(int operationTimeoutSeconds) throws Exception {
		// 1. checking preconditions and initialize
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPFileListCall fileListCall = (SCMPFileListCall) SCMPCallFactory.FILE_LIST_CALL.newInstance(this.requester,
				this.serviceName);
		try {
			fileListCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("list files failed ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("list files failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		String fileNameList = new String((byte[]) reply.getBody());
		String[] fileNames = fileNameList.split(Constants.FILE_LIST_DELIMITER);
		return Arrays.asList(fileNames);
	}

	private void createFileSession(int operationTimeoutSeconds) throws SCServiceException {
		// 1. checking preconditions and initialize
		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		createSessionCall.setEchoIntervalSeconds(Constants.DEFAULT_FILE_SESSION_TIMEOUT_SECONDS);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("create file session failed ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("create file session failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		this.sessionId = reply.getSessionId();
	}

	private synchronized void deleteFileSession(int operationTimeoutSeconds) throws Exception {
		// 1. checking preconditions and initialize
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("delete file session failed ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("delete file session failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.sessionId = null;
		}
	}
}