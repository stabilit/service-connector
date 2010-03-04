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
package com.stabilit.sc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.stabilit.sc.app.server.Server;
import com.stabilit.sc.exception.SCKernelException;

/**
 * @author JTraber
 * 
 */
public class SCKernel implements SCKernelConstants, Runnable{

	private static final String HTTP_CONFIG_FILE = "sc-http.properties";
	private static final String TCP_CONFIG_FILE = "sc-tcp.properties";
	private static Properties props;

	public static void main(String[] args) throws SCKernelException {
		SCKernel.startHTTPSCServer();
		SCKernel.startTCPSCServer();
	}

	public static Properties loadProperties(String fileName) throws SCKernelException {
		Properties props = new Properties();

		FileInputStream is = null;
		try {
			is = new FileInputStream(fileName);
			props.load(is);
		} catch (FileNotFoundException e1) {
			try {
				props.load(SCKernel.class.getResourceAsStream(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			throw new SCKernelException("Config file: " + fileName + "could not be loaded!");
		}
		return props;
	}

	public static void startHTTPSCServer() throws SCKernelException {
		props = SCKernel.loadProperties(HTTP_CONFIG_FILE);
		Server server = new Server();
		server.setProps(props);
		Thread thread = new Thread(server);
		thread.start();
	}

	public static void startTCPSCServer() throws SCKernelException {
		props = SCKernel.loadProperties(TCP_CONFIG_FILE);
		Server server = new Server();
		server.setProps(props);
		Thread thread = new Thread(server);
		thread.start();
	}

	//TODO wieder entfernen, runnable zu testzwecken!
	@Override
	public void run() {
		try {
			SCKernel.startHTTPSCServer();
			SCKernel.startTCPSCServer();
		} catch (SCKernelException e) {
			e.printStackTrace();
		}
	}
}
