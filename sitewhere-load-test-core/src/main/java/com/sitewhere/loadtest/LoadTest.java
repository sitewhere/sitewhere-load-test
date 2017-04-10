/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest;

import com.sitewhere.loadtest.spi.server.ILoadTestServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Main class for accessing core load test node functionality.
 * 
 * @author Derek
 */
public class LoadTest {

    /** Singleton server instance */
    private static ILoadTestServer SERVER;

    /**
     * Called once to bootstrap the server.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	SERVER = new LoadTestServer();

	// Initialize server components and check for errors.
	SERVER.initialize(monitor);
	if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
	    throw SERVER.getLifecycleError();
	}

	// Start server components and check for errors.
	SERVER.lifecycleStart(monitor);
	if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
	    throw SERVER.getLifecycleError();
	}
    }

    /**
     * Called to shut down the server.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public static void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getServer().lifecycleStop(monitor);

	// Handle errors that prevent server shutdown.
	if (SERVER.getLifecycleStatus() == LifecycleStatus.Error) {
	    throw SERVER.getLifecycleError();
	}
    }

    /**
     * Get the singleton server instance.
     * 
     * @return
     */
    public static ILoadTestServer getServer() {
	if (SERVER == null) {
	    throw new RuntimeException("Load test server has not been initialized.");
	}
	return SERVER;
    }

    /**
     * Determine whether server is available.
     * 
     * @return
     */
    public static boolean isServerAvailable() {
	return ((SERVER != null && (SERVER.getLifecycleStatus() == LifecycleStatus.Started)));
    }
}