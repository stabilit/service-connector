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
package com.stabilit.tcp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author JTraber
 * 
 */
public class MultipleSocketServer implements Runnable {

	private Socket connection;
	private String TimeStamp;
	private int ID;

	public MultipleSocketServer(Socket s, int i) {
		this.connection = s;
		this.ID = i;
	}

	public static void main(String[] args) {
		int port = 6789;
		int count = 0;
		try {
			ServerSocket socket1 = new ServerSocket(port);
			System.out.println("MultipleSocketServer Initialized");
			while (true) {
				Socket connection = socket1.accept();
				Runnable runnable = new MultipleSocketServer(connection,
						++count);
				Thread thread = new Thread(runnable);
				thread.start();
			}
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			BufferedInputStream is = new BufferedInputStream(connection
					.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			int character;
			StringBuffer process = new StringBuffer();
			while ((character = isr.read()) != 10) {
				process.append((char) character);
			}
			System.out.println(process);
			// need to wait 10 seconds to pretend that we're processing
			// something
//			try {
//				Thread.sleep(10000);
//			} catch (Exception e) {
//			}
			TimeStamp = new java.util.Date().toString();
			String returnCode = "MultipleSocketServer repsonded at \n"
					+ TimeStamp + '\n';
			DataOutputStream outToClient = new DataOutputStream(connection
					.getOutputStream());
			outToClient.writeBytes(returnCode);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
//				connection.close();
			} catch (Exception e) {
			}
		}
	}
}
