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

package org.serviceconnector.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * SystemInfo holds and detects current system information.
 * 
 * @author JTraber
 */
public class SystemInfo {

	private final static Logger log = Logger.getLogger(SystemInfo.class);

	private static final String JAVA_VERSION = "java.version";
	private static final String JAVA_VM_VERSION = "java.vm.version";
	private static final String USER_DIR = "user.dir";
	private static final String OS_NAME = "os.name";
	private static final String SUN_OS_PATCH_LEVEL = "sun.os.patch.level";
	private static final String USER_TIMEZONE = "user.timezone";
	private static final String USER_COUNTRY = "user.country";
	private static final String OS_ARCH = "os.arch";

	private static String javaVersion;
	private static String vmVersion;
	private static String serverIPAddress;
	private static String localHostId;
	private static long totalHeapMemory;
	private static String os;
	private static String osPatchLevel;
	private static String cpuType;
	private static String userDir;
	private static String countrySetting;
	private static String userTimezone;
	private static int utcOffset;
	private static boolean useDST;
	private static long totalPhysMemory;
	private static long availPhysMemory;
	private static long availDiskMemory;
	private static String processorType;
	private static int processorSpeed;
	private static Date localDate;
	private static int numberOfProcessors;

	static {
		loadInfos();
	}

	private static void loadInfos() {
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
		
//		Sigar sig = new Sigar();
//		try {
//			Mem mem = sig.getMem();
//			totalPhysMemory = mem.getTotal();
//			availPhysMemory = mem.getActualFree();
//
//			CpuInfo[] cpuInfos = sig.getCpuInfoList();
//			processorSpeed = cpuInfos[0].getMhz();
//			processorType = cpuInfos[0].getModel();
//			numberOfProcessors = cpuInfos[0].getTotalCores();
//
//			FileSystemUsage fileSystem = sig.getFileSystemUsage(userDir);
//			availDiskMemory = fileSystem.getAvail();
//		} catch (SigarException e) {
//			log.error("Processor Information could not be detected, SIGAR didn't work properly: " + e.getMessage());
//		}

		try {
			localHostId = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			log.error("Local Host Identification could not be detected: " + e.getMessage());
		}
	}

	/**
	 * Returns current used java version.
	 * 
	 * @return javaVersion
	 */
	public static String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * Returns version of current used java virtual machine.
	 * 
	 * @return vmVersion
	 */
	public static String getVmVersion() {
		return vmVersion;
	}

	/**
	 * Returns server IP address.
	 * 
	 * @return serverIPAddress
	 */
	public static String getServerIPAddress() {
		return serverIPAddress;
	}

	/**
	 * Returns local host identification (hostname / IP).
	 * 
	 * @return localHostId.
	 */
	public static String getLocalHostId() {
		return localHostId;
	}

	/**
	 * Returns current operating system.
	 * 
	 * @return os
	 */
	public static String getOs() {
		return os;
	}

	/**
	 * Returns operation system patch level.
	 * 
	 * @return osPatchLevel
	 */
	public static String getOsPatchLevel() {
		return osPatchLevel;
	}

	/**
	 * Returns CPU type.
	 * 
	 * @return cpuType
	 */
	public static String getCpuType() {
		return cpuType;
	}

	/**
	 * Returns current user directory.
	 * 
	 * @return userDir
	 */
	public static String getUserDir() {
		return userDir;
	}

	/**
	 * Returns the current country setting.
	 * 
	 * @return countrySetting
	 */
	public static String getCountrySetting() {
		return countrySetting;
	}

	/**
	 * Returns the current user time zone.
	 * 
	 * @return userTimezone
	 */
	public static String getUserTimezone() {
		return userTimezone;
	}

	/**
	 * Returns the offset to universal time coordinated (UTC).
	 * 
	 * @return utcOffset
	 */
	public static int getUtcOffset() {
		return utcOffset;
	}

	/**
	 * Returns true if daylight saving (DST) time is used.
	 * 
	 * @return useDST
	 */
	public static boolean isUseDST() {
		return useDST;
	}

	/**
	 * Returns local date.
	 * 
	 * @return localDate
	 */
	public static Date getLocalDate() {
		return localDate;
	}

	/**
	 * Returns the number of processors.
	 * 
	 * @return numberOfProcessors
	 */
	public static int getNumberOfProcessors() {
		return numberOfProcessors;
	}

	/**
	 * Returns total heap memory in bytes.
	 * 
	 * @return totalHeapMemory
	 */
	public static long getTotalHeapMemory() {
		return totalHeapMemory;
	}

	/**
	 * Returns total physical memory in bytes.
	 * 
	 * @return totalPhysMemory
	 */
	public static long getTotalPhysMemory() {
		return totalPhysMemory;
	}

	/**
	 * Returns current available physical memory in bytes.
	 * 
	 * @return availPhysMemory
	 */
	public static long getAvailPhysMemory() {
		return availPhysMemory;
	}

	/**
	 * Returns processor type.
	 * 
	 * @return processorType
	 */
	public static String getProcessorType() {
		return processorType;
	}

	/**
	 * Returns processor speed in megahertz.
	 * 
	 * @return processorSpeed
	 */
	public static int getProcessorSpeed() {
		return processorSpeed;
	}

	/**
	 * Returns total free bytes on file system available in bytes.
	 * 
	 * @return availDiskMemory
	 */
	public static long getAvailDiskMemory() {
		return availDiskMemory;
	}
}