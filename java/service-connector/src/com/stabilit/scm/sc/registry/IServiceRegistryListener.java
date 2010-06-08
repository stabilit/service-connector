package com.stabilit.scm.sc.registry;

import java.util.EventListener;

public interface IServiceRegistryListener extends EventListener {

	public abstract void serviceRegistryEvent(ServiceRegistryEvent serviceRegistryEvent);

}
