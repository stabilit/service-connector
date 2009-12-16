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
package com.stabilit.environment.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * EnvLog is responsible for writing a log file with current system information.
 * Detecting the information is done by the class Environment.
 * 
 * @author JTraber
 */
public class EnvLog {

	private static final int MILLISEC_TO_H = 3600000;
	private static final int BYTE_TO_MEGABYTE = 1024 * 1024;

	private Logger log = Logger.getLogger(EnvLog.class);
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            Name of the Log file to be created.
	 */
	public EnvLog(String fileName) {
		super();
		this.fileName = fileName;
	}

	/**
	 * Starts the EnvLog stand alone.
	 * 
	 * @param args
	 *            Name of the Log file to be created.
	 */
	public static void main(String[] args) {
		EnvLog logFile = new EnvLog(args[0]);
		logFile.createLogFile();
	}

	/**
	 * Creates the complete log File.
	 */
	public void createLogFile() {
		File logFile = new File(fileName);
		BufferedWriter bf = null;
		try {
			bf = new BufferedWriter(new FileWriter(logFile));

			Environment env = new Environment();
			env.loadEnvironment();

			String currentDT = env.getLocalDate().toString();
			bf.write(currentDT + " Detect JRE Version          : "
					+ env.getJavaVersion());
			bf.newLine();
			bf.write(currentDT + " Detect VM  Version          : "
					+ env.getVmVersion());
			bf.newLine();
			bf.write(currentDT + " Local Host Identification   : "
					+ env.getLocalHostId());
			bf.newLine();
			bf.write(currentDT + " Total Heap Memory           : "
					+ (env.getTotalHeapMemory() / BYTE_TO_MEGABYTE)
					+ " Megabyte");
			bf.newLine();
			bf.write(currentDT + " Operation System            : "
					+ env.getOs());
			bf.newLine();
			bf.write(currentDT + " Operation Patch Level       : "
					+ env.getOsPatchLevel());
			bf.newLine();
			bf.write(currentDT + " CPU Type                    : "
					+ env.getCpuType());
			bf.newLine();
			bf.write(currentDT + " User Directory              : "
					+ env.getUserDir());
			bf.newLine();
			bf.write(currentDT + " Country Setting             : "
					+ env.getCountrySetting());
			bf.newLine();
			bf
					.write(currentDT
							+ " Timezone / Universal Time Coordinated (UTC) Offset / Daylight saving time (DST) : "
							+ env.getUserTimezone() + " / +"
							+ (env.getUtcOffset() / MILLISEC_TO_H) + "h / "
							+ env.isUseDST());
			bf.newLine();
			bf.write(currentDT + " Total Phys. Memory          : "
					+ (env.getTotalPhysMemory() / BYTE_TO_MEGABYTE)
					+ " Megabyte");
			bf.newLine();
			bf.write(currentDT + " Total Avail. Memory         : "
					+ (env.getAvailPhysMemory() / BYTE_TO_MEGABYTE)
					+ " Megabyte");
			bf.newLine();
			bf.write(currentDT + " Processorinfo               : "
					+ env.getProcessorType());
			bf.newLine();
			bf.write(currentDT + " Processorspeed              : "
					+ env.getProcessorSpeed() + " Megahertz");
			bf.newLine();
			bf.write(currentDT + " Number of Processors        : "
					+ env.getNumberOfProcessors());
			bf.flush();
		} catch (IOException e) {
			log.error("File Writer for Logfile (" + fileName
					+ " ) couldn't get instanced.");
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				log.error("File Writer for Logfile (" + fileName
						+ " ) couldn't get closed.");
			}
		}
	}
}