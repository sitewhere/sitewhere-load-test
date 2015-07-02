/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.server;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.sitewhere.loadtest.spi.server.IConfigurationResolver;
import com.sitewhere.loadtest.version.IVersion;
import com.sitewhere.spi.SiteWhereException;

/**
 * Resolves load test configuration relative to the Tomcat installation base directory.
 * 
 * @author Derek
 */
public class TomcatConfigurationResolver implements IConfigurationResolver {

	/** Static logger instance */
	public static Logger LOGGER = Logger.getLogger(TomcatConfigurationResolver.class);

	/** File name for SiteWhere server config file */
	public static final String CONFIG_FILE_NAME = "sitewhere-loadtest.xml";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.loadtest.spi.server.IConfigurationResolver#resolveLoadTestContext
	 * (com.sitewhere.loadtest.version.IVersion)
	 */
	@Override
	public ApplicationContext resolveLoadTestContext(IVersion version) throws SiteWhereException {
		LOGGER.info("Loading Spring configuration ...");
		File sitewhereConf = getConfigurationFolder();
		File configFile = new File(sitewhereConf, CONFIG_FILE_NAME);
		if (!configFile.exists()) {
			throw new SiteWhereException("SiteWhere load test node configuration not found: "
					+ configFile.getAbsolutePath());
		}
		GenericApplicationContext context = new GenericApplicationContext();

		// Read context from XML configuration file.
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		reader.loadBeanDefinitions(new FileSystemResource(configFile));

		context.refresh();
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.IConfigurationResolver#getConfigurationRoot()
	 */
	@Override
	public File getConfigurationRoot() throws SiteWhereException {
		return TomcatConfigurationResolver.getConfigurationFolder();
	}

	/**
	 * Gets the CATALINA/conf/sitewhere folder where configs are stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static File getConfigurationFolder() throws SiteWhereException {
		String catalina = System.getProperty("catalina.base");
		if (catalina == null) {
			throw new SiteWhereException("CATALINA_HOME not set.");
		}
		File catFolder = new File(catalina);
		if (!catFolder.exists()) {
			throw new SiteWhereException("CATALINA_HOME folder does not exist.");
		}
		File confDir = new File(catalina, "conf");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf folder does not exist.");
		}
		File sitewhereDir = new File(confDir, "loadtest");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf/loadtest folder does not exist.");
		}
		return sitewhereDir;
	}

	/**
	 * Gets the CATALINA/data folder where data is stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static File getSiteWhereDataFolder() throws SiteWhereException {
		String catalina = System.getProperty("catalina.base");
		if (catalina == null) {
			throw new SiteWhereException("CATALINA_HOME not set.");
		}
		File catFolder = new File(catalina);
		if (!catFolder.exists()) {
			throw new SiteWhereException("CATALINA_HOME folder does not exist.");
		}
		File dataDir = new File(catalina, "data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		return dataDir;
	}
}