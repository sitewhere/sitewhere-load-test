/* 
 * Copyright (C) SiteWhere, LLC - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package com.sitewhere.loadtest.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gets information about system version from properties generated into the
 * classpath.
 * 
 * @author Derek
 */
public class Version implements IVersion {

    /** Serial version UID */
    private static final long serialVersionUID = -5288060387857352607L;

    /** Loaded from classpath to get version information */
    private static Properties properties = new Properties();

    static {
	try (final InputStream stream = Version.class.getClassLoader()
		.getResourceAsStream("META-INF/application-build.properties")) {
	    properties.load(stream);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.loadtest.version.IVersion#getVersionIdentifier()
     */
    @Override
    public String getVersionIdentifier() {
	return properties.getProperty("sitewhere.version");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.loadtest.version.IVersion#getBuildTimestamp()
     */
    @Override
    public String getBuildTimestamp() {
	return properties.getProperty("build.timestamp");
    }
}