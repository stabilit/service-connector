package org.serviceconnector.test.integration.srv;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.ISCPublishServer;
import org.serviceconnector.api.srv.ISCServerCallback;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.sc.service.SCServiceException;

public class PublishConnectionTypeTcpTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishConnectionTypeTcpTest.class);

	private int threadCount = 0;
	private ISCPublishServer server;
	private Exception ex;

	private static Process scProcess;
	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSC0Properties, TestConstants.scProperties0);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSC0Properties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		threadCount = Thread.activeCount();
		server = new SCPublishServer();
		server.startListener(TestConstants.HOST, 9001, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000,
				TestConstants.serviceNamePublish, 1, 1, new CallBack());
	}

	@After
	public void tearDown() throws Exception {
		server.deregisterServer(TestConstants.serviceNamePublish);
		server.destroyServer();
		server = null;
		ex = null;
		assertEquals("number of threads", threadCount, Thread.activeCount());
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceNameNull_throwsValidatorException() throws Exception {
		server.publish(null, TestConstants.mask, "something");
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceNameEmpty_throwsValidatorException() throws Exception {
		server.publish("", TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameWhiteSpace_throwsSCException() throws Exception {
		server.publish(" ", TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameOneChar_throwsSCException() throws Exception {
		server.publish("a", TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameNotExistingService_throwsSCException() throws Exception {
		server.publish("notExistingService", TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceName32LongString_throwsSCException() throws Exception {
		server.publish(TestConstants.stringLength32, TestConstants.mask, "something");
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceName33LongString_throwsValidatorException() throws Exception {
		server.publish(TestConstants.stringLength33, TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameDisabled_throwsSCException() throws Exception {
		server.publish(TestConstants.serviceNamePublishDisabled, TestConstants.mask, "something");
	}

	@Test
	public void publish_serviceNameValid_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameSessionServiceWithoutRegistering_throwsSCException()
			throws Exception {
		server.publish(TestConstants.serviceName, TestConstants.mask, "something");
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameSessionServiceWithRegistering_throwsSCException()
			throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT9000,
				TestConstants.serviceName, 1, 1, new CallBack());
		try {
			server.publish(TestConstants.serviceName, TestConstants.mask, "something");
		} catch (Exception e) {
			ex = e;
		} finally {
			server.deregisterServer(TestConstants.serviceName);
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_maskNull_throwsValidatorException() throws Exception {
		server.publish(TestConstants.serviceNamePublish, null, "something");
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_maskEmpty_throwsValidatorException() throws Exception {
		server.publish(TestConstants.serviceNamePublish, "", "something");
	}

	@Test
	public void publish_maskWhiteSpace_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, " ", "something");
	}

	@Test
	public void publish_maskOneChar_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, "a", "something");
	}

	@Test
	public void publish_maskPangram_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.pangram, "something");
	}

	@Test
	public void publish_mask256LongString_passes() throws Exception {
		server
				.publish(TestConstants.serviceNamePublish, TestConstants.stringLength256,
						"something");
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_mask257LongString_throwsValidatorException() throws Exception {
		server
				.publish(TestConstants.serviceNamePublish, TestConstants.stringLength257,
						"something");
	}

	@Test
	public void publish_maskValidWithoutPercentSign_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, "something");
	}

	@Test
	public void publish_maskValidWithPercentSign_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish,
				"0000121%%%%%%%%%%%%%%%-----------X-----------", "something");
	}

	@Test
	public void publish_dataNull_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, null);
	}

	@Test
	public void publish_dataEmpty_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, "");
	}

	@Test
	public void publish_dataWhiteSpace_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, " ");
	}

	@Test
	public void publish_dataOneChar_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, "a");
	}

	@Test
	public void publish_dataPangram_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, TestConstants.pangram);
	}

	@Test
	public void publish_data1000LongString_passes() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("a");
		}
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, sb.toString());
	}

	@Test
	public void publish_data10000LongString_passes() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append("a");
		}
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, sb.toString());
	}

	// TODO FJU I don't know the intended behavior of this
	@Test
	public void publish_dataEmptyObject_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, new Object());
	}

	@Test
	public void publish_dataSCMessage_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask, new SCMessage());
	}

	@Test
	public void publish_data60kBArray_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask,
				new byte[TestConstants.dataLength60kB]);
	}

	@Test
	public void publish_data1MBArray_passes() throws Exception {
		server.publish(TestConstants.serviceNamePublish, TestConstants.mask,
				new byte[TestConstants.dataLength1MB]);
	}
	
	@Test
	public void publish_10000Messages_passes() throws Exception {
		for (int i = 0; i < 10000; i++) {
			server.publish(TestConstants.serviceNamePublish, TestConstants.mask, new byte[128]);
		}
	}

	private class CallBack implements ISCServerCallback {
	}
}
