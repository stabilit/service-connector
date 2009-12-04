package com.stabilit.environment;

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
	private String serverIPAdress;
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

	public Environment() {
	}

	public void loadEnvironment() {
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
		} catch (SigarException e1) {
			log.error("Processor Information could not be detected, SIGAR didn't work properly!");
		}		

		try {
			localHostId = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			log.error("Local Host Identification could not be detected.");
		}
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getVmVersion() {
		return vmVersion;
	}

	public void setVmVersion(String vmVersion) {
		this.vmVersion = vmVersion;
	}

	public String getServerIPAdress() {
		return serverIPAdress;
	}

	public void setServerIPAdress(String serverIPAdress) {
		this.serverIPAdress = serverIPAdress;
	}

	public String getLocalHostId() {
		return localHostId;
	}

	public void setLocalHostId(String localHostId) {
		this.localHostId = localHostId;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsPatchLevel() {
		return osPatchLevel;
	}

	public void setOsPatchLevel(String osPatchLevel) {
		this.osPatchLevel = osPatchLevel;
	}

	public String getCpuType() {
		return cpuType;
	}

	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}

	public String getUserDir() {
		return userDir;
	}

	public void setUserDir(String userDir) {
		this.userDir = userDir;
	}

	public String getCountrySetting() {
		return countrySetting;
	}

	public void setCountrySetting(String countrySetting) {
		this.countrySetting = countrySetting;
	}

	public String getUserTimezone() {
		return userTimezone;
	}

	public void setUserTimezone(String userTimezone) {
		this.userTimezone = userTimezone;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	public boolean isUseDST() {
		return useDST;
	}

	public void setUseDST(boolean useDST) {
		this.useDST = useDST;
	}

	public Date getLocalDate() {
		return localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public int getNumberOfProcessors() {
		return numberOfProcessors;
	}

	public void setNumberOfProcessors(int numberOfProcessors) {
		this.numberOfProcessors = numberOfProcessors;
	}

	public long getTotalHeapMemory() {
		return totalHeapMemory;
	}

	public void setTotalHeapMemory(long totalHeapMemory) {
		this.totalHeapMemory = totalHeapMemory;
	}

	public long getTotalPhysMemory() {
		return totalPhysMemory;
	}

	public void setTotalPhysMemory(long totalPhysMemory) {
		this.totalPhysMemory = totalPhysMemory;
	}

	public long getAvailPhysMemory() {
		return availPhysMemory;
	}

	public void setAvailPhysMemory(long availPhysMemory) {
		this.availPhysMemory = availPhysMemory;
	}

	public String getProcessorType() {
		return processorType;
	}

	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}

	public int getProcessorSpeed() {
		return processorSpeed;
	}

	public void setProcessorSpeed(int processorSpeed) {
		this.processorSpeed = processorSpeed;
	}
}