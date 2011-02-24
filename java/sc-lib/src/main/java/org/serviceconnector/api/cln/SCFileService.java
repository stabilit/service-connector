/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.api.cln;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPFileDownloadCall;
import org.serviceconnector.call.SCMPFileListCall;
import org.serviceconnector.call.SCMPFileUploadCall;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class SCFileService. SCFileService is a remote interface in client API to a file service and provides communication
 * functions.
 */
public class SCFileService extends SCService {

	/**
	 * Instantiates a new sC file service.
	 * 
	 * @param scClient
	 *            the SC client
	 * @param serviceName
	 *            the service name
	 * @param requester
	 *            the requester
	 */
	public SCFileService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
	}

	/**
	 * Upload file with default operation timeout.
	 * 
	 * @param remoteFileName
	 *            the remote file name to store the file
	 * @param inStream
	 *            stream to upload
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void uploadFile(String remoteFileName, InputStream inStream) throws Exception {
		this.uploadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, inStream);
	}

	/**
	 * Upload file.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param remoteFileName
	 *            the remote file name to store the file
	 * @param inStream
	 *            stream to upload
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void uploadFile(int operationTimeoutSeconds, String remoteFileName, InputStream inStream) throws Exception {
		// 1. checking preconditions and initialize
		// create file session
		this.createFileSession(operationTimeoutSeconds);
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPFileUploadCall uploadFileCall = new SCMPFileUploadCall(this.requester, this.serviceName, this.sessionId);
			uploadFileCall.setRequestBody(inStream);
			uploadFileCall.setRemoteFileName(remoteFileName);
			try {
				uploadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Upload file failed. ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Upload file failed.");
				ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			// always delete file session
			this.deleteFileSession(operationTimeoutSeconds);
		}
	}

	/**
	 * Download file with default operation timeout.
	 * 
	 * @param remoteFileName
	 *            the remote name of the file
	 * @param outStream
	 *            the out stream to store download
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void downloadFile(String remoteFileName, OutputStream outStream) throws Exception {
		this.downloadFile(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, remoteFileName, outStream);
	}

	/**
	 * Download file.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @param remoteFileName
	 *            the remote name of the file
	 * @param outStream
	 *            the out stream to store download
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void downloadFile(int operationTimeoutSeconds, String remoteFileName, OutputStream outStream)
			throws Exception {
		// 1. checking preconditions and initialize
		// create file session
		this.createFileSession(operationTimeoutSeconds);
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPFileDownloadCall downloadFileCall = new SCMPFileDownloadCall(this.requester, this.serviceName, this.sessionId);
			downloadFileCall.setRemoteFileName(remoteFileName);
			try {
				downloadFileCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Download file failed. ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Download file failed.");
				ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			// 4. post process, reply to client
			if (reply.isComposite()) {
				((SCMPCompositeReceiver) reply).writeBodyAsStream(outStream);
				return;
			}
			outStream.write((byte[]) reply.getBody());
		} finally {
			// always delete file session
			this.deleteFileSession(operationTimeoutSeconds);
		}
	}

	/**
	 * List files with default operation timeout.
	 * 
	 * @return the list of files on the remote server
	 * @throws Exception
	 *             the exception
	 */
	public synchronized List<String> listFiles() throws Exception {
		return this.listFiles(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * List files.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @return the list of files on the remote server
	 * @throws Exception
	 *             the exception
	 */
	public synchronized List<String> listFiles(int operationTimeoutSeconds) throws Exception {
		// 1. checking preconditions and initialize
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPFileListCall fileListCall = new SCMPFileListCall(this.requester, this.serviceName);
		try {
			fileListCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("List files failed. ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("List files failed.");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		String fileNameList = new String((byte[]) reply.getBody());
		String[] fileNames = fileNameList.split(Constants.FILE_LIST_DELIMITER);
		return Arrays.asList(fileNames);
	}

	/**
	 * Creates the file session.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @throws SCServiceException
	 *             the sC service exception
	 */
	private void createFileSession(int operationTimeoutSeconds) throws SCServiceException {
		// 1. checking preconditions and initialize
		this.requester.getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, this.serviceName);
		createSessionCall.setEchoIntervalSeconds(Constants.DEFAULT_FILE_SESSION_TIMEOUT_SECONDS);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("Create file session failed. ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("Create file session failed.");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		this.sessionId = reply.getSessionId();
	}

	/**
	 * Delete file session.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @throws Exception
	 *             the exception
	 */
	private synchronized void deleteFileSession(int operationTimeoutSeconds) throws Exception {
		// 1. checking preconditions and initialize
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, this.serviceName,
					this.sessionId);
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Delete file session failed. ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Delete file session failed.");
				ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.sessionId = null;
		}
	}
}