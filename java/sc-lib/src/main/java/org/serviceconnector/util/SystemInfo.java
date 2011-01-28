/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
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

	/** The Constant log. */
	private final static Logger log = Logger.getLogger(SystemInfo.class);

	/** The Constant JAVA_VERSION. */
	private static final String JAVA_VERSION = "java.version";	
	/** The Constant JAVA_VM_VERSION. */
	private static final String JAVA_VM_VERSION = "java.vm.version";	
	/** The Constant USER_DIR. */
	private static final String USER_DIR = "user.dir";	
	/** The Constant OS_NAME. */
	private static final String OS_NAME = "os.name";	
	/** The Constant SUN_OS_PATCH_LEVEL. */
	private static final String SUN_OS_PATCH_LEVEL = "sun.os.patch.level";	
	/** The Constant USER_TIMEZONE. */
	private static final String USER_TIMEZONE = "user.timezone";	
	/** The Constant USER_COUNTRY. */
	private static final String USER_COUNTRY = "user.country";	
	/** The Constant OS_ARCH. */
	private static final String OS_ARCH = "os.arch";

	/** The config file name. */
	private static String configFileName;	
	/** The java version. */
	private static String javaVersion;	
	/** The vm version. */
	private static String vmVersion;
	/** The local host id. */
	private static String localHostId;	
	/** The max memory. */
	private static long maxMemory;	
	/** The os. */
	private static String os;	
	/** The os patch level. */
	private static String osPatchLevel;
		/** The cpu type. */
	private static String cpuType;	
	/** The user dir. */
	private static String userDir;	
	/** The country setting. */
	private static String countrySetting;	
	/** The user timezone. */
	private static String userTimezone;	
	/** The utc offset. */
	private static int utcOffset;	
	/** The use dst. */
	private static boolean useDST;	
	/** The free memory. */
	private static long freeMemory;	
	/** The total memory. */
	private static long totalMemory;	
	/** The available disk space. */
	private static long availableDiskSpace;	
	/** The local date. */
	private static Date startupDateTime;	
	/** The available processors. */
	private static int availableProcessors;

	static {
		loadInfos();
	}

	/**
	 * Load infos.
	 */
	private static void loadInfos() {

		Properties sysprops = System.getProperties();

		javaVersion = sysprops.getProperty(JAVA_VERSION);
		vmVersion = sysprops.getProperty(JAVA_VM_VERSION);
		os = sysprops.getProperty(OS_NAME);
		osPatchLevel = sysprops.getProperty(SUN_OS_PATCH_LEVEL);
		cpuType = sysprops.getProperty(OS_ARCH);
		userDir = sysprops.getProperty(USER_DIR);
		countrySetting = sysprops.getProperty(USER_COUNTRY);
		maxMemory = Runtime.getRuntime().maxMemory();
		freeMemory = Runtime.getRuntime().freeMemory();
		totalMemory = Runtime.getRuntime().totalMemory();
		
		TimeZone zone = TimeZone.getDefault();
		userTimezone = sysprops.getProperty(USER_TIMEZONE);
		utcOffset = zone.getRawOffset();
		useDST = zone.useDaylightTime();
		startupDateTime = new Date(System.currentTimeMillis());
		availableProcessors = Runtime.getRuntime().availableProcessors();

		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
		memBean.getHeapMemoryUsage();

		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			availableDiskSpace += roots[i].getFreeSpace();
		}

		try {
			localHostId = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			log.error("Local Host Identification could not be detected: " + e.getMessage());
		}
	}

	/**
	 * Gets the config file name.
	 * 
	 * @return the config file name
	 */
	public static String getConfigFileName() {
		return configFileName;
	}

	/**
	 * Sets the config file name.
	 * 
	 * @param configFileName
	 *            the new config file name
	 */
	public static void setConfigFileName(String configFileName) {
		SystemInfo.configFileName = configFileName;
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
	 * Returns startup date and time.
	 * 
	 * @return startupDateTime
	 */
	public static Date getStartupDateTime() {
		return startupDateTime;
	}

	/**
	 * Returns the number of processors.
	 * 
	 * @return numberOfProcessors
	 */
	public static int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * Gets the max memory.
	 *
	 * @return the max memory
	 */
	public static long getMaxMemory() {
		maxMemory = Runtime.getRuntime().maxMemory();
		return maxMemory;
	}

	/**
	 * Gets the free memory.
	 *
	 * @return the free memory
	 */
	public static long getFreeMemory() {
		freeMemory = Runtime.getRuntime().freeMemory();
		return freeMemory;
	}

	/**
	 * Gets the total memory.
	 *
	 * @return the total memory
	 */
	public static long getTotalMemory() {
		totalMemory = Runtime.getRuntime().totalMemory();
		return totalMemory;
	}

	/**
	 * Returns total free bytes on file system available in bytes.
	 * 
	 * @return availDiskMemory
	 */
	public static long getAvailableDiskSpace() {
		return availableDiskSpace;
	}
	
	/**
	 * Gets the thread info.
	 *
	 * @return the thread info
	 */
	public static SystemThreadInfo getThreadInfo() {
		return new SystemThreadInfo();
	}
}