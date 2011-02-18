package org.serviceconnector.scmp;

import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.service.Subscription;

public interface ISubscriptionCallback extends ISCMPMessageCallback {

	public abstract Subscription getSubscription();

	public abstract IRequest getRequest();
}
