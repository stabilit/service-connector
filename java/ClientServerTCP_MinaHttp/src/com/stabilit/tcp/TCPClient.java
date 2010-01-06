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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author JTraber
 * 
 */
public class TCPClient {

	public static void main(String[] args) throws Exception {
		String sentence = "Hello World! Das ist ein Satz, welcher ganz genau 128 bytes benötigt wobei einige Wörter natürlich aus Füllzwecken hier sind:";
		String modifiedSentence;		
		int numberOfMsg = Integer.valueOf(args[0]);
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket
				.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfMsg; i++) {			
			outToServer = new DataOutputStream(clientSocket
					.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			outToServer.writeBytes(sentence + i + '\n');
			modifiedSentence = inFromServer.readLine();
			//System.out.println("FROM SERVER: " + modifiedSentence);			
		}
		clientSocket.close();
		long endTime = System.currentTimeMillis();
		long neededTime = endTime - startTime;
		System.out.println("Job Done in: " + neededTime + " Ms");
		double neededSeconds = neededTime / 1000d;
		System.out.println((numberOfMsg * 1 / neededSeconds)
				+ " Messages in 1 second!");
		System.out.println("Anzahl clients: " + 1);
		System.out
				.println("Anzahl Messages pro client: " + numberOfMsg);
	}
}
