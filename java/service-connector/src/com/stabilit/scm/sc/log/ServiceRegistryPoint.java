package com.stabilit.scm.sc.log;

import java.util.EventListener;

import com.stabilit.scm.common.log.listener.ListenerSupport;
import com.stabilit.scm.common.scmp.SCMPMessage;

enum ServiceRegistryEventType {
   UNDEFINED, ALLOCATE, DEALLOCATE;	
}

public final class ServiceRegistryPoint extends ListenerSupport<IServiceRegistryListener> {

	private static ServiceRegistryPoint serviceRegistryPoint = new ServiceRegistryPoint();

	private ServiceRegistryPoint() {
	}

	public static ServiceRegistryPoint getInstance() {
		return serviceRegistryPoint;
	}

	public void fireAllocate(Object source, SCMPMessage scmpMessage) {
		if (getInstance().isEmpty() == false) {
			ServiceRegistryEvent serviceRegistryEvent = new ServiceRegistryEvent(source, scmpMessage);
			ServiceRegistryPoint.getInstance().fireServiceRegistry(serviceRegistryEvent);
		}
	}

	public void fireDeallocate(Object source, SCMPMessage scmpMessage) {
		if (getInstance().isEmpty() == false) {
			ServiceRegistryEvent serviceRegistryEvent = new ServiceRegistryEvent(source, scmpMessage);
			serviceRegistryEvent.setEventType(ServiceRegistryEventType.DEALLOCATE);
			ServiceRegistryPoint.getInstance().fireServiceRegistry(serviceRegistryEvent);
		}
	}
	
	public void fireServiceRegistry(ServiceRegistryEvent serviceRegistryEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IServiceRegistryListener serviceRegistryListener = (IServiceRegistryListener) localArray[i];
				serviceRegistryListener.serviceRegistryEvent(serviceRegistryEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
