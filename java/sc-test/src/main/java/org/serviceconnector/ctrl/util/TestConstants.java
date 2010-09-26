package org.serviceconnector.ctrl.util;

public class TestConstants {
	public static final String LOCALHOST = "localhost";
	public static final String HOST = "localhost";
	public static final int PORT_HTTP = 8080;
	public static final int PORT_TCP = 9000;
	public static final int PORT_MIN = 1;
	public static final int PORT_MAX = 65535;

	public static final String log4jSrvProperties = "log4jSrv.properties";

	public static final String log4jSC0Properties = "log4jSC0.properties";
	public static final String log4jSC1Properties = "log4jSC1.properties";
	public static final String scProperties0 = "scIntegration.properties";
	public static final String scProperties1 = "scIntegrationChanged.properties";
	public static final String scPropertiesCascaded = "scCascaded.properties";
	
	public static final String serviceName = "simulation";
	public static final String serviceNameAlt = "P01_RTXS_sc1";
	public static final String serviceNameSessionDisabled = "disabledService";
	public static final String serviceNamePublish = "publish-simulation";
	public static final String serviceNamePublishDisabled = "disabledPublish";
	
	public static final String pangram = "The quick brown fox jumps over a lazy dog.";
	public static final String stringLength32 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength33 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength256 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength257 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	
	public static final int dataLength60kB = 61440;
	public static final int dataLength1MB = 1048576;
	
	public static final String pidLogFile = "pid.log";
	
	public static final String sessionSrv = "session";
	public static final String publishSrv = "publish";
	
	public static final String mask = "0000121ABCDEFGHIJKLMNO-----------X-----------";

}
