package org.serviceconnetor;

public class TestConstants {
	public static final String LOCALHOST = "localhost";
	public static final String HOST = "localhost";
	public static final int PORT_HTTP = 7000;
	public static final int PORT_TCP = 9000;
	public static final int PORT_MIN = 1;
	public static final int PORT_MAX = 65535;
	public static final int PORT_LISTENER = 30000; 

	public static final String log4jSrvProperties = "log4j-srv.properties";

	public static final String log4jSCProperties = "log4j-sc.properties";
	public static final String log4jSCcascadedProperties = "log4j-sc-cascaded.properties";
	public static final String SCProperties = "sc.properties";
	public static final String SCcascadedProperties = "sc-cascaded.properties";
	
	public static final String sessionServiceName = "session-1";
	public static final String publishServiceName = "publish-1";
	
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

	public static final String logDirectoryToken = "log-directory";
	public static final String scRunable = "sc.jar";
	public static final String serverRunable = "test-server.jar";
}
