/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.api;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCMessage. A SCMessage is the basic transport unit to communicate with a Service Connector.
 * 
 * @author JTraber
 */
public class SCMessage {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMessage.class);

	/** The message info. */
	private String messageInfo;
	/** The session info. */
	private String sessionInfo;
	/** The compressed - regards data part of the message. */
	private boolean compressed;
	/** The data. */
	private Object data;
	/** The session id - identifies session context of communication. */
	private String sessionId;
	/** The cache id. */
	private String cacheId;
	/** The service name. */
	private String serviceName;
	/** The application error code. */
	private int appErrorCode;
	/** The application error text. */
	private String appErrorText;
	/** The reject flag used to reject a create session / subscribe. */
	private boolean reject;
	/** The cache expiration date time, format yyyy-MM-dd'T'hh:mm:ss.SSSZ. */
	private String cacheExpirationDateTime;

	/**
	 * Instantiates a new SCMessage.
	 */
	public SCMessage() {
		this.messageInfo = null;
		// default of compression is true
		this.compressed = Constants.DEFAULT_COMPRESSION_FLAG;
		this.data = null;
		this.sessionId = null;
		this.sessionInfo = null;
		this.cacheId = null;
		this.appErrorCode = -1;
		this.appErrorText = null;
		this.reject = false;
	}

	public SCMessage(byte[] data) {
		this();
		this.data = data;
	}

	public SCMessage(String data) {
		this();
		this.data = data;
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            Optional information passed together with the message body that helps to identify the message content without
	 *            investigating the body.<br>
	 *            Any printable character, length > 0 and < 256 Byte<br>
	 *            Example: SECURITY_MARKET_QUERY
	 * @throws SCMPValidatorException
	 */
	public void setMessageInfo(String messageInfo) throws SCMPValidatorException {
		if (messageInfo == null) {
			return;
		}
		ValidatorUtility.validateStringLength(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
		this.messageInfo = messageInfo;
	}

	/**
	 * Gets the session info.
	 * 
	 * @return the session info
	 */
	public String getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            Optional information passed together with the message body Any printable character, length > 0 and < 256 Byte<br>
	 * @throws SCMPValidatorException
	 */
	public void setSessionInfo(String sessionInfo) throws SCMPValidatorException {
		if (sessionInfo == null) {
			return;
		}
		ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
		this.sessionInfo = sessionInfo;
	}

	/**
	 * Gets the message info.
	 * 
	 * @return the message info
	 */
	public String getMessageInfo() {
		return messageInfo;
	}

	/**
	 * Checks if is compressed.
	 * 
	 * @return the boolean
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * Sets the compressed. Default is true.
	 * 
	 * @param compressed
	 *            Regards the data part of the message.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the new data
	 */
	public void setData(Object data) {
		if (data == null) {
			this.data = null;
			return;
		}
		if (data instanceof byte[]) {
			this.data = data;
			return;
		}
		if (data instanceof String) {
			this.data = data;
			return;
		}
		throw new InvalidParameterException("this type of body is not supported");
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Object getData() {
		return this.data;
	}

	/**
	 * Gets the data length.
	 * 
	 * @return the data length
	 */
	public int getDataLength() {
		if (this.data == null) {
			return 0;
		}
		if (byte[].class == this.data.getClass()) {
			return ((byte[]) this.data).length;
		}
		if (String.class == this.data.getClass()) {
			return ((String) this.data).length();
		}
		if (this.data instanceof InputStream) {
			/*
			 * needs to be different in case of INPUT_STREAM body length is always unknown for streams. Set it on Integer.MAX_VALUE
			 * 2^31-1 (2048 MB). Never rely on bodyLength for body type INPUT_STREAM.
			 */
			return Integer.MAX_VALUE;
		}
		return 0;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Gets the cache id.
	 * 
	 * @return the cache id
	 */
	public String getCacheId() {
		return cacheId;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name. Needs to be the same service which is used to send the message. The name will be overwritten at the
	 * time the message is sent.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Sets the cache id.
	 * 
	 * @param cacheId
	 *            the new cache id
	 */
	public void setCacheId(String cacheId) throws SCMPValidatorException {
		if (cacheId == null) {
			return;
		}
		ValidatorUtility.validateStringLength(1, cacheId, 256, SCMPError.HV_WRONG_SESSION_INFO);
		this.cacheId = cacheId;
	}

	/**
	 * Gets the application error code.
	 * 
	 * @return the application error code
	 */
	public int getAppErrorCode() {
		return this.appErrorCode;
	}

	/**
	 * Sets the application error text.
	 * 
	 * @param appErrorText
	 *            the new application error text
	 * @throws SCMPValidatorException
	 */
	public void setAppErrorText(String appErrorText) throws SCMPValidatorException {
		ValidatorUtility.validateStringLength(1, appErrorText, 256, SCMPError.HV_WRONG_APP_ERROR_TEXT);
		this.appErrorText = appErrorText;
	}

	/**
	 * Gets the application error text.
	 * 
	 * @return the application error text
	 */
	public String getAppErrorText() {
		return this.appErrorText;
	}

	/**
	 * Checks if is reject.
	 * 
	 * @return true, if is reject
	 */
	public boolean isReject() {
		return this.reject;
	}

	/**
	 * Sets the reject.
	 * 
	 * @param reject
	 *            the new reject
	 */
	public void setReject(boolean reject) {
		this.reject = reject;
	}

	/**
	 * Gets the cache expiration date time.
	 * 
	 * @return the cache expiration date time
	 */
	public String getCacheExpirationDateTime() {
		return cacheExpirationDateTime;
	}

	/**
	 * Sets the cache expiration date time.
	 * 
	 * @param cacheExpirationDateTime
	 *            the new cache expiration date time
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void setCacheExpirationDateTime(Date cacheExpirationDateTime) throws SCMPValidatorException {
		SimpleDateFormat format = new SimpleDateFormat(Constants.CED_DATE_FORMAT);
		this.cacheExpirationDateTime = format.format(cacheExpirationDateTime);
	}

	/**
	 * Sets the cache expiration date time. Format has to be yyyy-MM-dd'T'hh:mm:ss.SSSZ.
	 * 
	 * @param cacheExpirationDateTime
	 *            the new cache expiration date time
	 * @throws SCMPValidatorException
	 */
	public void setCacheExpirationDateTime(String cacheExpirationDateTime) throws SCMPValidatorException {
		SimpleDateFormat format = new SimpleDateFormat(Constants.CED_DATE_FORMAT);
		format.setLenient(false);

		try {
			format.parse(cacheExpirationDateTime);
		} catch (Exception e) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "wrong format of cacheExpirationDateTime should be "
					+ Constants.CED_DATE_FORMAT);
		}
		this.cacheExpirationDateTime = cacheExpirationDateTime;
	}

	/**
	 * Sets the application error code.
	 * 
	 * @param appErrorCode
	 *            the new application error code
	 * @throws SCMPValidatorException
	 */
	public void setAppErrorCode(int appErrorCode) throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, appErrorCode, SCMPError.HV_WRONG_APP_ERROR_CODE);
		this.appErrorCode = appErrorCode;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SCMessage [messageInfo=");
		builder.append(messageInfo);
		builder.append(", sessionInfo=");
		builder.append(sessionInfo);
		builder.append(", compressed=");
		builder.append(compressed);
		builder.append(", sessionId=");
		builder.append(sessionId);
		builder.append(", cacheId=");
		builder.append(cacheId);
		builder.append("]");
		return builder.toString();
	}
}