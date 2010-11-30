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

	private final static Logger log = Logger.getLogger(SystemInfo.class);

	private static final String JAVA_VERSION = "java.version";
	private static final String JAVA_VM_VERSION = "java.vm.version";
	private static final String USER_DIR = "user.dir";
	private static final String OS_NAME = "os.name";
	private static final String SUN_OS_PATCH_LEVEL = "sun.os.patch.level";
	private static final String USER_TIMEZONE = "user.timezone";
	private static final String USER_COUNTRY = "user.country";
	private static final String OS_ARCH = "os.arch";

	private static String configFileName;
	private static String javaVersion;
	private static String vmVersion;
	private static String localHostId;
	private static long maxMemory;
	private static String os;
	private static String osPatchLevel;
	private static String cpuType;
	private static String userDir;
	private static String countrySetting;
	private static String userTimezone;
	private static int utcOffset;
	private static boolean useDST;
	private static long freeMemory;
	private static long totalMemory;
	private static long availableDiskSpace;
	private static Date localDate;
	private static int availableProcessors;

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
		maxMemory = Runtime.getRuntime().maxMemory();
		freeMemory = Runtime.getRuntime().freeMemory();
		totalMemory = Runtime.getRuntime().totalMemory();
		
		TimeZone zone = TimeZone.getDefault();
		userTimezone = sysprops.getProperty(USER_TIMEZONE);
		utcOffset = zone.getRawOffset();
		useDST = zone.useDaylightTime();
		localDate = new Date(System.currentTimeMillis());
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
}