/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.server;

import java.io.File;

import org.springframework.context.ApplicationContext;

import com.sitewhere.loadtest.version.IVersion;
import com.sitewhere.spi.SiteWhereException;

/**
 * Allows for pluggable implementations that can resolve the Spring configuration for the
 * system.
 * 
 * @author Derek
 */
public interface IConfigurationResolver {

	/**
	 * Resolves the load test Spring configuration context.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public ApplicationContext resolveLoadTestContext(IVersion version) throws SiteWhereException;

	/**
	 * Gets the root {@link File} where load test configuration files are stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public File getConfigurationRoot() throws SiteWhereException;
}