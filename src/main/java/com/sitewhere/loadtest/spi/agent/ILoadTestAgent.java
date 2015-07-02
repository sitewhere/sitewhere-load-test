/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Common interface implemented by all load test agents.
 * 
 * @author Derek
 */
public interface ILoadTestAgent extends ILifecycleComponent {

	/**
	 * Get unique agent id.
	 * 
	 * @return
	 */
	public String getAgentId();
}