/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.sitewhere.loadtest.LoadTest;

/**
 * Handles server shutdown logic when servlet context is destroyed.
 * 
 * @author Derek
 */
public class ShutdownListener implements ServletContextListener {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(ShutdownListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent
	 * )
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LoadTest.getLoadTestServer().lifecycleStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
	}
}