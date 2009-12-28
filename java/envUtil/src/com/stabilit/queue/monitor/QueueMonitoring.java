package com.stabilit.queue.monitor;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sun.jdmk.comm.HtmlAdaptorServer;

public class QueueMonitoring {

	public static void main(String[] args) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mxbeanName = new ObjectName(
				"com.stabilit.queue.monitor:type=RequestQueuer");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		RequestQueuer mxbean = RequestQueuer.getInstance();
		mxbean.add(new Request("requestString1", 1, new Date(), map));
		mxbean.add(new Request("requestString2", 2, new Date(), map));
		mxbean.add(new Request("requestString3", 3, new Date(), map));
		mxbean.add(new Request("requestString4", 4, new Date(), map));
		mxbean.add(new Request("requestString5", 5, new Date(), map));
		mxbean.add(new Request("requestString6", 6, new Date(), map));
		mxbean.add(new Request("requestString7", 7, new Date(), map));
		mxbean.add(new Request("requestString8", 8, new Date(), map));
		mxbean.add(new Request("requestString9", 9, new Date(), map));
		mxbean.add(new Request("requestString10", 10, new Date(), map));
		mxbean.add(new Request("requestString11", 11, new Date(), map));

		mbs.registerMBean(mxbean, mxbeanName);
		
		int portNumber=9393;
	    ObjectName htmlName = new ObjectName(
	       "MyApp:name=MyAppHtmlAdaptor,port="+portNumber) ;
	    HtmlAdaptorServer html = new HtmlAdaptorServer(portNumber);
	    html.setPort(portNumber);
	    mbs.registerMBean(html, htmlName);
	    html.start();

		System.out.println("Waiting for incoming requests...");
		Thread.sleep(Long.MAX_VALUE);
	}
}