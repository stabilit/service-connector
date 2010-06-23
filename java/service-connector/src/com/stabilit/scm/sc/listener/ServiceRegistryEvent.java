package com.stabilit.scm.sc.listener;

import java.util.EventObject;

import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.sc.registry.ServiceRegistry;

/**
 * The Class ServiceRegistryEvent.
 */
public class ServiceRegistryEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1136098816676253453L;
	
	/** The event type. */
	private ServiceRegistryEventType eventType;
	
	/** The scmp. */
	private SCMPMessage scmp;
	
	/**
	 * Instantiates a new service registry event.
	 *
	 * @param source the source
	 * @param scmp the scmp
	 */
	public ServiceRegistryEvent(Object source, SCMPMessage scmp) {
		super(source);
		this.scmp = scmp;
		this.eventType = ServiceRegistryEventType.UNDEFINED;
	}
	
	/**
	 * Gets the sCMP.
	 *
	 * @return the sCMP
	 */
	public SCMPMessage getSCMP() {
		return scmp;
	}
	
	/**
	 * Gets the registry size.
	 *
	 * @return the registry size
	 */
	public int getRegistrySize() {
		ServiceRegistry serviceRegistry = (ServiceRegistry) this.getSource();
		if (serviceRegistry == null) {
			return 0;
		}
		return serviceRegistry.getSize();
	}

	/**
	 * Sets the event type.
	 *
	 * @param eventType the new event type
	 */
	public void setEventType(ServiceRegistryEventType eventType) {
		this.eventType = eventType;
	}

}
