/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Main class for accessing core load test node functionality.
 * 
 * @author Derek
 */
public class LoadTester {

	/** Singleton server instance */
	private static LoadTesterServer SERVER;

	/**
	 * Called once to bootstrap the load test node.
	 * 
	 * @throws SiteWhereException
	 */
	public static void start() throws SiteWhereException {
		SERVER = new LoadTesterServer();
		SERVER.lifecycleStart();

		// Handle errors that prevent server startup.
		if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
			throw SERVER.getLifecycleError();
		}
	}

	/**
	 * Called to shut down the load test node.
	 * 
	 * @throws SiteWhereException
	 */
	public static void stop() throws SiteWhereException {
		getLoadTestServer().lifecycleStop();
	}

	/**
	 * Get the singleton load test node instance.
	 * 
	 * @return
	 */
	public static LoadTesterServer getLoadTestServer() {
		if (SERVER == null) {
			throw new RuntimeException("Load test node has not been initialized.");
		}
		return SERVER;
	}
}