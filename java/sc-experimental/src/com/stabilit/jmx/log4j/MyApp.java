package com.stabilit.jmx.log4j;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sun.jdmk.comm.HtmlAdaptorServer;

public class MyApp implements MyAppMBean {

	private static Logger logger = Logger.getLogger(MyApp.class);

	public void go() throws Exception {
		while (true) {
			logger.debug("DEBUG");
			logger.info("INFO");
			Thread.sleep(2000);
		}
	}

	public void setLoggingLevel(String level) {
		logger.info("Setting logging level to: " + level);
		Level newLevel = Level.toLevel(level, Level.INFO);
		Logger.getRootLogger().setLevel(newLevel);
	}

	public String getLoggingLevel() {
		return Logger.getRootLogger().getLevel().toString();
	}
	
	public static void main(String[] args) throws Exception{
	    MyApp app = new MyApp() ;
	    ObjectName objName = new ObjectName("MyApp:name=MyApp");
	    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	    server.registerMBean(app, objName);

	    int portNumber=9393;
	    ObjectName htmlName = new ObjectName(
	       "MyApp:name=MyAppHtmlAdaptor,port="+portNumber) ;
	    HtmlAdaptorServer html = new HtmlAdaptorServer(portNumber);
	    html.setPort(portNumber);
	    server.registerMBean(html, htmlName);
	    html.start();
	    app.go();
	}
}
