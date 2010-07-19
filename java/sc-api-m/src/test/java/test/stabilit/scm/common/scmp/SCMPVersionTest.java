package test.stabilit.scm.common.scmp;

import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.scmp.SCMPVersion;

/**
 * The Class SCMPVersionTest.
 * 
 * @author JTrnka
 */
public class SCMPVersionTest {
	/**
	 * version compatibility tests.
	 */
	@Test
	public void versionCompatibilityTest0() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported(SCMPVersion.TEST.toString());
	}

	@Test
	public void versionCompatibilityTest1() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.2");	//TEST = 3.2
	}
	
	@Test
	public void versionCompatibilityTest2() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.1");
	}

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest3() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.3");
	}

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest4() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("2.0");
	}	
	
	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest5() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("4.0");
	}	

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest10() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("A.b");
	}	

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest11() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("11");
	}
}
