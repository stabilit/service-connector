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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Environment holds and detects current system information.
 * 
 * @author JTraber
 */
public class Environment {

	private Logger log = Logger.getLogger(Environment.class);

	private static final String JAVA_VERSION = "java.version";
	private static final String JAVA_VM_VERSION = "java.vm.version";
	private static final String USER_DIR = "user.dir";
	private static final String OS_NAME = "os.name";
	private static final String SUN_OS_PATCH_LEVEL = "sun.os.patch.level";
	private static final String USER_TIMEZONE = "user.timezone";
	private static final String USER_COUNTRY = "user.country";
	private static final String OS_ARCH = "os.arch";

	private String javaVersion;
	private String vmVersion;
	private String serverIPAddress;
	private String localHostId;
	private long totalHeapMemory;
	private String os;
	private String osPatchLevel;
	private String cpuType;
	private String userDir;
	private String countrySetting;
	private String userTimezone;
	private int utcOffset;
	private boolean useDST;
	private long totalPhysMemory;
	private long availPhysMemory;
	private String processorType;
	private int processorSpeed;
	private Date localDate;
	private int numberOfProcessors;

	/**
	 * Detects current system information.
	 */
	public void loadEnvironment() {
		try {
			detectEnv();
		} catch (Throwable t) {
			log.error("Environment could not been properly loaded: "
					+ t.getMessage());
		}
	}

	private void detectEnv() throws EnvLoadException {
		Properties sysprops = System.getProperties();

		javaVersion = sysprops.getProperty(JAVA_VERSION);
		vmVersion = sysprops.getProperty(JAVA_VM_VERSION);
		os = sysprops.getProperty(OS_NAME);
		osPatchLevel = sysprops.getProperty(SUN_OS_PATCH_LEVEL);
		cpuType = sysprops.getProperty(OS_ARCH);
		userDir = sysprops.getProperty(USER_DIR);
		countrySetting = sysprops.getProperty(USER_COUNTRY);
		totalHeapMemory = Runtime.getRuntime().maxMemory();

		TimeZone zone = TimeZone.getDefault();
		userTimezone = sysprops.getProperty(USER_TIMEZONE);
		utcOffset = zone.getRawOffset();
		useDST = zone.useDaylightTime();
		localDate = new Date(System.currentTimeMillis());

		Sigar sig = new Sigar();
		try {
			Mem mem = sig.getMem();
			totalPhysMemory = mem.getTotal();
			availPhysMemory = mem.getActualFree();

			CpuInfo[] cpuInfos = sig.getCpuInfoList();
			processorSpeed = cpuInfos[0].getMhz();
			processorType = cpuInfos[0].getModel();
			numberOfProcessors = cpuInfos[0].getTotalCores();
		} catch (SigarException e) {
			log
					.error("Processor Information could not be detected, SIGAR didn't work properly: "
							+ e.getMessage());
			throw new EnvLoadException(e);
		}

		try {
			localHostId = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			log.error("Local Host Identification could not be detected: "
					+ e.getMessage());
			throw new EnvLoadException(e);
		}
	}

	/**
	 * Returns current used java version.
	 * 
	 * @return javaVersion
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * Returns version of current used java virtual machine.
	 * 
	 * @return vmVersion
	 */
	public String getVmVersion() {
		return vmVersion;
	}

	/**
	 * Returns server IP address.
	 * 
	 * @return serverIPAddress
	 */
	public String getServerIPAddress() {
		return serverIPAddress;
	}

	/**
	 * Returns local host identification (hostname / IP).
	 * 
	 * @return localHostId.
	 */
	public String getLocalHostId() {
		return localHostId;
	}

	/**
	 * Returns current operating system.
	 * 
	 * @return os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * Returns operation system patch level.
	 * 
	 * @return osPatchLevel
	 */
	public String getOsPatchLevel() {
		return osPatchLevel;
	}

	/**
	 * Returns CPU type.
	 * 
	 * @return cpuType
	 */
	public String getCpuType() {
		return cpuType;
	}

	/**
	 * Returns current user directory.
	 * 
	 * @return userDir
	 */
	public String getUserDir() {
		return userDir;
	}

	/**
	 * Returns the current country setting.
	 * 
	 * @return countrySetting
	 */
	public String getCountrySetting() {
		return countrySetting;
	}

	/**
	 * Returns the current user time zone.
	 * 
	 * @return userTimezone
	 */
	public String getUserTimezone() {
		return userTimezone;
	}

	/**
	 * Returns the offset to universal time coordinated (UTC).
	 * 
	 * @return utcOffset
	 */
	public int getUtcOffset() {
		return utcOffset;
	}

	/**
	 * Returns true if daylight saving (DST) time is used.
	 * 
	 * @return useDST
	 */
	public boolean isUseDST() {
		return useDST;
	}

	/**
	 * Returns local date.
	 * 
	 * @return localDate
	 */
	public Date getLocalDate() {
		return localDate;
	}

	/**
	 * Returns the number of processors.
	 * 
	 * @return numberOfProcessors
	 */
	public int getNumberOfProcessors() {
		return numberOfProcessors;
	}

	/**
	 * Returns total heap memory in bytes.
	 * 
	 * @return totalHeapMemory
	 */
	public long getTotalHeapMemory() {
		return totalHeapMemory;
	}

	/**
	 * Returns total physical memory in bytes.
	 * 
	 * @return totalPhysMemory
	 */
	public long getTotalPhysMemory() {
		return totalPhysMemory;
	}

	/**
	 * Returns current available physical memory in bytes.
	 * 
	 * @return availPhysMemory
	 */
	public long getAvailPhysMemory() {
		return availPhysMemory;
	}

	/**
	 * Returns processor type.
	 * 
	 * @return processorType
	 */
	public String getProcessorType() {
		return processorType;
	}

	/**
	 * Returns processor speed in megahertz.
	 * 
	 * @return processorSpeed
	 */
	public int getProcessorSpeed() {
		return processorSpeed;
	}
}