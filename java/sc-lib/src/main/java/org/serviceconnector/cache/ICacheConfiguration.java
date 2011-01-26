/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.cache;

/**
 * The Interface ICacheConfiguration.
 */
public interface ICacheConfiguration {

	/**
	 * Checks if is cache enabled.
	 * 
	 * @return true, if is cache enabled
	 */
	public abstract boolean isCacheEnabled();

	/**
	 * Gets the disk path.
	 * 
	 * @return the disk path
	 */
	public abstract String getDiskPath();

	/**
	 * Gets the max elements in memory.
	 * 
	 * @return the max elements in memory
	 */
	public abstract int getMaxElementsInMemory();

	/**
	 * Gets the max elements on disk.
	 * 
	 * @return the max elements on disk
	 */
	public abstract int getMaxElementsOnDisk();

}