package com.stabilit.sc.factory;

public interface IAbstractFactory extends IFactory {

	public IFactoryLoader getFactoryLoader();
	 
	public void setFactoryLoader(IFactoryLoader factoryLoader);
	
	public IFactory newInstance() throws FactoryException;
}
