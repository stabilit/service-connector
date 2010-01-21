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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.HrefStatus;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.ResponseHandler;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author JTraber
 *
 */
public class ResponseHandlerFile implements ResponseHandler {

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondBadRequest(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondBadRequest(Resource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondConflict(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.lang.String)
	 */
	@Override
	public void respondConflict(Resource resource, Response response,
			Request request, String message) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondContent(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.util.Map)
	 */
	@Override
	public void respondContent(Resource resource, Response response,
			Request request, Map<String, String> params)
			throws NotAuthorizedException, BadRequestException {
		try {
			((FsFileResource)resource).sendContent(response.getOutputStream(), null, null, null);
			response.getOutputStream().close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondCreated(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondCreated(Resource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondExpectationFailed(com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondExpectationFailed(Response response, Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondHead(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondHead(Resource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondMethodNotAllowed(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondMethodNotAllowed(Resource res, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondMethodNotImplemented(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondMethodNotImplemented(Resource resource,
			Response response, Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondNoContent(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondNoContent(Resource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondNotFound(com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondNotFound(Response response, Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondNotModified(com.bradmcevoy.http.GetableResource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondNotModified(GetableResource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondPartialContent(com.bradmcevoy.http.GetableResource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.util.Map, com.bradmcevoy.http.Range)
	 */
	@Override
	public void respondPartialContent(GetableResource resource,
			Response response, Request request, Map<String, String> params,
			Range range) throws NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondRedirect(com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.lang.String)
	 */
	@Override
	public void respondRedirect(Response response, Request request,
			String redirectUrl) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondUnauthorised(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request)
	 */
	@Override
	public void respondUnauthorised(Resource resource, Response response,
			Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#respondWithOptions(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.util.List)
	 */
	@Override
	public void respondWithOptions(Resource resource, Response response,
			Request request, List<Method> methodsAllowed) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResponseHandler#responseMultiStatus(com.bradmcevoy.http.Resource, com.bradmcevoy.http.Response, com.bradmcevoy.http.Request, java.util.List)
	 */
	@Override
	public void responseMultiStatus(Resource resource, Response response,
			Request request, List<HrefStatus> statii) {
		// TODO Auto-generated method stub

	}

}
