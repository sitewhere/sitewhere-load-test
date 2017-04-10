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

    /** REST API URL */
    private String restApiUrl;

    /** REST API username */
    private String restUsername;

    /** REST API password */
    private String restPassword;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.loadtest.spi.server.ISiteWhereConnection#getRestApiUrl()
     */
    public String getRestApiUrl() {
	return restApiUrl;
    }

    public void setRestApiUrl(String restApiUrl) {
	this.restApiUrl = restApiUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.loadtest.spi.server.ISiteWhereConnection#getRestUsername()
     */
    public String getRestUsername() {
	return restUsername;
    }

    public void setRestUsername(String restUsername) {
	this.restUsername = restUsername;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.loadtest.spi.server.ISiteWhereConnection#getRestPassword()
     */
    public String getRestPassword() {
	return restPassword;
    }

    public void setRestPassword(String restPassword) {
	this.restPassword = restPassword;
    }
}