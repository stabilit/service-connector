package org.serviceconnector.test.unit;

import java.io.File;
import java.io.FilenameFilter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.ctx.AppContext;

public class DumpFileTest {

	@Before
	public void beforeOneTest() throws Exception {
		AppContext.init();
	}

	@After
	public void afterOneTest() throws Exception {
		AppContext.destroy();
	}

	/**
	 * Description: Starts and configures AppContext with configFile, creates dump file<br>
	 * Expectation: creates dump file in directory
	 */
	@Test
	public void t01_dumpFile() throws Exception {
		AppContext.initConfiguration(TestConstants.SC0Properties);
		AppContext.getBasicConfiguration().load(AppContext.getApacheCompositeConfig());
		String dumpPathString = AppContext.getBasicConfiguration().getDumpPath();
		// delete directory
		File dumpPath = new File(dumpPathString);
		TestUtil.deleteDir(dumpPath);
		AppContext.dump();

		String[] dumpFiles = dumpPath.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(Constants.XML_EXTENSION) && name.startsWith(Constants.DUMP_FILE_NAME);
			}
		});
		Assert.assertTrue("dump file has not been created", dumpFiles.length > 0);
	}
}
