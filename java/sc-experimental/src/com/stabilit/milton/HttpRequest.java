/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.milton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import com.bradmcevoy.common.ContentTypeUtils;
import com.bradmcevoy.http.AbstractRequest;
import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.RequestParseException;
import com.bradmcevoy.http.Response;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * @author JTraber
 * 
 */
public class HttpRequest extends AbstractRequest {

	private InputStream in;
	private Map<Header, String> headers;
	private String url;
	private String userName;
	private Method method;	

	public HttpRequest(InputStream in, Map<Header, String> headers, String url, String userName,
			Method method) {
		super();
		this.in = in;
		this.headers = headers;
		this.url = url;
		this.userName = userName;
		this.method = method;
	}

	@Override
	public String getRequestHeader(Header header) {
		return headers.get(header);
	}

	@Override
	public String getAbsoluteUrl() {
		return url;
	}

	@Override
	public Auth getAuthorization() {
		if (userName == null)
			return null;
		return new Auth(userName);
	}

	@Override
	public String getFromAddress() {
		return headers.get(Header.HOST);
	}

	@Override
	public Map<String, String> getHeaders() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return in;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public void parseRequestParameters(Map<String, String> params, Map<String, FileItem> files)
			throws RequestParseException {
		try {
			if (isMultiPart()) {
				DiskFileItem fileItem = new DiskFileItem("",ContentTypeUtils.findContentTypes("txt"), false, "1.txt", 0, new File("webdav/in/1.txt"));
//				UploadListener listener = new UploadListener();
//				MonitoredDiskFileItemFactory factory = new MonitoredDiskFileItemFactory(listener);
//				ServletFileUpload upload = new ServletFileUpload(factory);
//				List items = upload.parseRequest(request);
//
//				parseQueryString(params);
//
//				for (Object o : items) {
//					FileItem item = (FileItem) o;
//					if (item.isFormField()) {
//						params.put(item.getFieldName(), item.getString());
//					} else {
//						files.put(item.getFieldName(), new FileItemWrapper(item));
//					}
//				}
			} else {
//				for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
//					String nm = (String) en.nextElement();
//					String val = request.getParameter(nm);
//					log.debug("..param: " + nm + " = " + val);
//					params.put(nm, val);
//				}
			}
//		} catch (FileUploadException ex) {
//			throw new RequestParseException("FileUploadException", ex);
		} catch (Throwable ex) {
			throw new RequestParseException(ex.getMessage(), ex);
		}

	}

	private boolean isMultiPart() {
		return (headers.get(Header.CONTENT_TYPE) == Response.MULTIPART);
	}
}
