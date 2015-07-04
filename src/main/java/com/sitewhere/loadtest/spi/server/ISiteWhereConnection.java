/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.server;

/**
 * SiteWhere connection information.
 * 
 * @author Derek
 */
public interface ISiteWhereConnection {

	/**
	 * Get API URL for interacting with SiteWhere instance.
	 * 
	 * @return
	 */
	public String getSiteWhereApiUrl();
}