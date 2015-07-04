/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.server;

import com.sitewhere.loadtest.spi.server.ISiteWhereConnection;

/**
 * Default implementation of {@link ISiteWhereConnection}.
 * 
 * @author Derek
 */
public class SiteWhereConnection implements ISiteWhereConnection {

	private String siteWhereApiUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.server.ISiteWhereConnection#getSiteWhereApiUrl()
	 */
	public String getSiteWhereApiUrl() {
		return siteWhereApiUrl;
	}

	public void setSiteWhereApiUrl(String siteWhereApiUrl) {
		this.siteWhereApiUrl = siteWhereApiUrl;
	}
}