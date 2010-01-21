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

import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Response;

/**
 * @author JTraber
 * 
 */
public class MiltonStarter {

	public static void main(String args[]) {
		MiltonStarter starter = new MiltonStarter();
		starter.run();
	}
	
	public void run() {
		HttpManager manager = new HttpManager(new FileSystemResourceFactory(), new ResponseHandlerFile());
		Request request = new com.stabilit.milton.HttpRequest();
		Response response = new com.stabilit.milton.HttpResponse();
		manager.process(request, response);		
	}
}
