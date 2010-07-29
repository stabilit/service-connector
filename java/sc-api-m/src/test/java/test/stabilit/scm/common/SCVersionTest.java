package test.stabilit.scm.common;

import org.junit.Test;

import com.stabilit.scm.common.SCVersion;
import com.stabilit.scm.common.cmd.SCMPValidatorException;

/**
 * The Class SCVersionTest.
 * 
 * @author JTrnka
 */
public final class SCVersionTest {

	/**
	 * version compatibility tests.
	 */
	@Test
	public void versionCompatibilityTest0() throws SCMPValidatorException {
		SCVersion.TEST.isSupported(SCVersion.TEST.toString());
	}

	@Test
	public void versionCompatibilityTest1() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-005");	//TEST = 3.2-5
	}
	
	@Test
	public void versionCompatibilityTest2() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-003");
	}

	@Test
	public void versionCompatibilityTest3() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.1-006");
	}

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest4() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.3-001");
	}

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest5() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("2.0-000");
	}	
	
	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest6() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("4.0-001");
	}	

	// formatting
	
	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest10() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-5");
	}	

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest11() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2.5");
	}	

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest12() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("a.b-c");
	}	

	@Test (expected=SCMPValidatorException.class)
	public void versionCompatibilityTest13() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("11");
	}
}

