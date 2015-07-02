/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.server;

import com.sitewhere.loadtest.spi.agent.IAgentManager;
import com.sitewhere.loadtest.version.IVersion;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Base interface for a load test server node.
 * 
 * @author Derek
 */
public interface ILoadTestServer extends ILifecycleComponent {

	/**
	 * Get server version information.
	 * 
	 * @return
	 */
	public IVersion getVersion();

	/**
	 * Get component that resolves how configuration information is loaded.
	 * 
	 * @return
	 */
	public IConfigurationResolver getConfigurationResolver();

	/**
	 * Get the agent manager implementation.
	 * 
	 * @return
	 */
	public IAgentManager getAgentManager();

	/**
	 * If server startup failed, return the exception that kept it from starting.
	 * 
	 * @return
	 */
	public ServerStartupException getServerStartupError();

	/**
	 * Store an exception that prevented server startup.
	 * 
	 * @param serverStartupError
	 */
	public void setServerStartupError(ServerStartupException serverStartupError);
}