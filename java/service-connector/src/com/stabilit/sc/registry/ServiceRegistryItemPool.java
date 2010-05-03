package com.stabilit.sc.registry;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.registry.ServerRegistry;

public class ServiceRegistryItemPool extends MapBean<String>{

	private int maxItems = -1;
	private SCMP scmp;
	private SocketAddress socketAddress;
	private IServerContext serverContext;
	private List<ServiceRegistryItem> freeItemList;
	private List<ServiceRegistryItem> allocatedItemList;
	
	public ServiceRegistryItemPool(SCMP scmp, SocketAddress socketAddress) {
		this.scmp = scmp;
		this.socketAddress = socketAddress;
		this.freeItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		this.allocatedItemList = Collections.synchronizedList(new ArrayList<ServiceRegistryItem>());
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
        this.serverContext = serverRegistry.getCurrentContext();
	}
	
	public IServerContext getServerContext() {
		return this.serverContext;
	}
	
	public SCMP getScmp() {
		return scmp;
	}
	
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public synchronized boolean isAvailable() {
		if (isNoLimit()) {
			// there is no limit
			return true;
		}
		return this.freeItemList.isEmpty() == false;
	}

	public synchronized boolean isNoLimit() {
		return this.maxItems < 0;
	}

	public synchronized ServiceRegistryItem getAvailableItem() {
		if (this.isAvailable() == false) {
			return null;
		}
		if (this.isNoLimit()) {
			ServiceRegistryItem item = new ServiceRegistryItem(this.scmp, this.socketAddress, this.serverContext);
			item.myItemPool = this;
			this.allocatedItemList.add(item);
			return item;
		}
		ServiceRegistryItem item = freeItemList.get(0);
		this.allocatedItemList.add(item);
		freeItemList.remove(item);
		return item;
	}
	
	public synchronized void freeItem(ServiceRegistryItem item) {
		this.allocatedItemList.remove(item);
		if (this.isNoLimit() == false) {
		    this.freeItemList.add(item);
		}
	}

}
