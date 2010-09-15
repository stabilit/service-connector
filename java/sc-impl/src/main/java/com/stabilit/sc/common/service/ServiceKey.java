package com.stabilit.sc.common.service;

/**
 * The Class SrvServiceKey. Represents the key to identify a service.
 */
public class ServiceKey {

	/** The host. */
	private String host;
	/** The port. */
	private int port;
	/** The service name. */
	private String serviceName;

	/**
	 * Instantiates a new service key.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param serviceName
	 *            the service name
	 */
	public ServiceKey(String host, int port, String serviceName) {
		super();
		this.host = host;
		this.port = port;
		this.serviceName = serviceName;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceKey other = (ServiceKey) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}
}