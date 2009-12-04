package com.stabilit.environment.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.stabilit.environment.Environment;

public class EnvLogFile {

	private static final int MS_TO_H = 3600000;
	private static final int B_TO_MB = 1024 * 1024;

	private Logger log = Logger.getLogger(EnvLogFile.class);
	private String fileName;

	public EnvLogFile(String fileName) {
		super();
		this.fileName = fileName;
	}

	public static void main(String[] args) {
		EnvLogFile logFile = new EnvLogFile(args[0]);
		logFile.createLogFile();
	}

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
					+ (env.getTotalHeapMemory() / B_TO_MB) + " MB");
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
			bf.write(currentDT + " Timezone / UTC Offset / DST : "
					+ env.getUserTimezone() + " / +"
					+ (env.getUtcOffset() / MS_TO_H) + "h / " + env.isUseDST());
			bf.newLine();
			bf.write(currentDT + " Total Phys. Memory          : "
					+ (env.getTotalPhysMemory() / B_TO_MB) + " MB");
			bf.newLine();
			bf.write(currentDT + " Total Avail. Memory         : "
					+ (env.getAvailPhysMemory() / B_TO_MB) + " MB");
			bf.newLine();
			bf.write(currentDT + " Processorinfo               : "
					+ env.getProcessorType());
			bf.newLine();
			bf.write(currentDT + " Processorspeed              : "
					+ env.getProcessorSpeed() + " MHz");
			bf.newLine();
			bf.write(currentDT + " Number of Processors        : "
					+ env.getNumberOfProcessors());
			bf.flush();
		} catch (IOException e) {
			log.error("File Writer couldn't get instanced.");
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				log.error("File Writer couldn't get closed.");
			}
		}
	}
}