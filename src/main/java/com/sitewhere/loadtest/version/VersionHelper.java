/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.version;

import com.sitewhere.spi.SiteWhereException;

/**
 * Used to access implementation class which is only included after Maven build.
 * 
 * @author Derek
 */
public class VersionHelper {

	/** Classname of dynamically loaded version identifier */
	private static final String CLASSNAME = "com.sitewhere.loadtest.version.Version";

	/**
	 * Hacky method of accessing a version file that is created at build time.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static IVersion getVersion() {
		Class<?> clazz;
		try {
			clazz = Class.forName(CLASSNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Version information file was not found on classpath.");
		}
		try {
			IVersion version = (IVersion) clazz.newInstance();
			return version;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}