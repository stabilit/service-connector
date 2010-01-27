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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.ProtocolHandlers;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.Request.Header;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.ettrema.http.fs.FileSystemResourceFactory;

/**
 * @author JTraber
 * 
 */
public class MiltonStarter {

	private static Map<Header, String> headers = new HashMap<Header, String>();
	private InputStream in;
	private OutputStream out;
	private HttpManager manager;

	public static void main(String args[]) {
		MiltonStarter starter = new MiltonStarter();
		starter.run();
	}

	public void run() {
		try {

			out = new FileOutputStream("webdav/nothing.txt");	
			manager = new HttpManager(new FileSystemResourceFactory(new File("webdav"),
					new SecurityManager() {

						@Override
						public Object authenticate(DigestResponse digestRequest) {
							System.out.println("SecurityManager authenticate(DigestResponse digestRequest)");
							return null;
						}

						@Override
						public Object authenticate(String user, String password) {
							System.out.println("SecurityManager authenticate(String user, String password)");
							return new Auth("");
						}

						@Override
						public boolean authorise(Request request, Method method, Auth auth, Resource resource) {
							System.out
									.println("SecurityManager authorise(Request request, Method method, Auth auth, Resource resource)");
							return true;
						}

						@Override
						public String getRealm() {
							System.out.println("SecurityManager getRealm");
							return null;
						}
					}), new DefaultWebDavResponseHandler(null), new ProtocolHandlers());
			// caseOne();
			// caseTwo();
//			caseThree();
//			caseFour();
			caseFive();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Simpler GET (download) 1 File
	public void caseOne() throws FileNotFoundException {
		headers.put(Header.HOST, "localhost");
		headers.put(Header.RANGE, "null");
		headers.put(Header.IF, "null");
		headers.put(Header.CONTENT_TYPE, Response.HTTP);
		in = new FileInputStream("webdav/in/download.txt");
		Request request = new HttpRequest(in, headers, "http://localhost/in/download.txt", "jot",
				Request.Method.GET);
		out = new FileOutputStream("webdav/out/downloaded.txt");
		Response response = new HttpResponse(out);
		manager.process(request, response);
	}

	// Simpler PUT (upload) 1 File
	public void caseTwo() throws FileNotFoundException {
		headers.put(Header.HOST, "localhost");
		headers.put(Header.RANGE, "null");
		headers.put(Header.IF, "1");
		headers.put(Header.CONTENT_TYPE, Response.HTTP);
		in = new FileInputStream("webdav/in/upload.txt");
		Request request = new HttpRequest(in, headers, "http://localhost/out/uploaded.txt", "jot",
				Request.Method.PUT);

		Response response = new HttpResponse(out);
		manager.process(request, response);
	}

	// Collection COPY (upload) several Files
	public void caseThree() throws FileNotFoundException {
		headers.put(Header.HOST, "localhost");
		headers.put(Header.RANGE, "null");
		headers.put(Header.IF, "1");
		headers.put(Header.CONTENT_TYPE, Response.HTTP);
		headers.put(Header.DESTINATION, "http://localhost/uploadCol/");

		Request request = new HttpRequest(in, headers, "http://localhost/in/", "jot", Request.Method.COPY);
		Response response = new HttpResponse(out);
		manager.process(request, response);
	}
	
	// Collection Delete several Files
	public void caseFour() throws FileNotFoundException {
		headers.put(Header.HOST, "localhost");
		headers.put(Header.RANGE, "null");
		headers.put(Header.IF, "1");
		headers.put(Header.CONTENT_TYPE, Response.HTTP);

		Request request = new HttpRequest(in, headers, "http://localhost/uploadCol/", "jot", Request.Method.DELETE);
		Response response = new HttpResponse(out);
		manager.process(request, response);
	}
	
	// Collection GET (download) several File
	public void caseFive() throws FileNotFoundException {
		headers.put(Header.HOST, "localhost");
		headers.put(Header.RANGE, "null");
		headers.put(Header.IF, "1");
		headers.put(Header.CONTENT_TYPE, Response.HTTP);
		
		out = new FileOutputStream("webdav/out/downloaded.txt");

		Request request = new HttpRequest(in, headers, "http://localhost/in/", "jot", Request.Method.GET);
		Response response = new HttpResponse(out);
		manager.process(request, response);
	}
}
