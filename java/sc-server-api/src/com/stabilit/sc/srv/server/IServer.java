/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
*/
/**
 * 
 */
package com.stabilit.sc.srv.server;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;

/**
 * @author JTraber
 *
 */
public interface IServer extends IFactoryable {

	public IServerContext getServerContext();
    public void setServerConfig(ServerConfigItem serverConfig);
	public void create() throws Exception;
	public void runAsync() throws Exception;
	public void runSync() throws Exception;
	public ServerConfigItem getServerConfig();
}
