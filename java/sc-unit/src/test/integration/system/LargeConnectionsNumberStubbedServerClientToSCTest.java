package system;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import vmstarters.StartSCSessionServer;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.service.SCMessage;


public class LargeConnectionsNumberStubbedServerClientToSCTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StubbedServerClientToSCTest.class);

	private static Process p;

	private static String userDir;

	private ISCClient client;

	private static final String host = "localhost";
	private static final int port8080 = 8080;
	private static final int port9000 = 9000;
	private static String serviceName = "simulation";
	private String serviceNameAlt = "P01_RTXS_sc1";

	private Exception ex;

	private static Thread serverThread;

	@BeforeClass
	public static void oneTimeSetUp() {

		userDir = System.getProperty("user.dir");
		String command = "java -Dlog4j.configuration=file:" + userDir
				+ "\\src\\test\\resources\\log4jSC0.properties -jar " + userDir
				+ "\\..\\service-connector\\target\\sc.jar -filename " + userDir
				+ "\\src\\test\\resources\\scIntegration.properties";

		try {
			p = Runtime.getRuntime().exec(command);

			// lets the SC load before starting communication
			Thread.sleep(1000);
			
			serverThread = new Thread("SERVER") {
				public void run() {
					try {
						StartSCSessionServer.main(new String[] { String.valueOf(port9000), serviceName,
								String.valueOf(10) });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			serverThread.start();
			
			// lets the Server load before starting communication
			Thread.sleep(1000);

		} catch (IOException e) {
			logger.error("oneTimeSetUp - IOExc", e);
		} catch (InterruptedException e) {
			logger.error("oneTimeSetUp - InterruptExc", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ISCClient client = new SCClient();
		while (true) {
			String sessions = client.workload(serviceName);
			if (Integer.parseInt(sessions.substring(0, sessions.indexOf('/'))) > 0) {
				break;
			}
			Thread.sleep(500);
		}
		System.out.println("6.\tTearDown proceed");
		ISessionService session = client.newSessionService(serviceName);
		session.createSession("sessionInfo", 300, 60);
		session.execute(new SCMessage("kill server"));
		Thread.sleep(500);

		p.destroy();
	}

	/**
	 * @throws java.lang.Exception
	 * 
	 *             Create a new SCClient for each test method.
	 */
	@Before
	public void setUp() throws Exception {
		
		try {
			client = new SCClient();
			while (!client.isAttached()) {
				System.out.println("2.5\tAttaching");
				try {
					client.attach(host, port8080);
				} catch (Exception e) {}
			}
			while (true) {
				String sessions = client.workload(serviceName);
				if (Integer.parseInt(sessions.substring(0, sessions.indexOf('/'))) > 0) {
					break;
				}
				Thread.sleep(500);
			}
		} finally {
			System.out.println("3.\tService is enabled!");
		}
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("5.\tTearDown");
	}
}
