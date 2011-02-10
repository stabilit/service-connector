package org.serviceconnector.casc;

import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.service.Subscription;

public interface ISubscriptionCallback extends ISCMPMessageCallback {

	public abstract Subscription getSubscription();

	public abstract IRequest getRequest();
}
