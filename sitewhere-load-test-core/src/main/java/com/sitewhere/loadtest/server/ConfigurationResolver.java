/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.server;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.sitewhere.loadtest.spi.server.IConfigurationResolver;
import com.sitewhere.loadtest.version.IVersion;
import com.sitewhere.spi.SiteWhereException;

/**
 * Resolves load test configuration relative to the "sitewhere.home" environment
 * variable.
 * 
 * @author Derek
 */
public class ConfigurationResolver implements IConfigurationResolver {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** File name for SiteWhere server config file */
    public static final String CONFIG_FILE_NAME = "sitewhere-loadtest.xml";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.loadtest.spi.server.IConfigurationResolver#
     * resolveLoadTestContext (com.sitewhere.loadtest.version.IVersion)
     */
    @Override
    public ApplicationContext resolveLoadTestContext(IVersion version) throws SiteWhereException {
	LOGGER.info("Loading Spring configuration ...");
	File conf = getConfigurationFolder();
	File configFile = new File(conf, CONFIG_FILE_NAME);
	if (!configFile.exists()) {
	    throw new SiteWhereException(
		    "SiteWhere load test node configuration not found: " + configFile.getAbsolutePath());
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
     * @see com.sitewhere.spi.configuration.IConfigurationResolver#
     * getConfigurationRoot()
     */
    @Override
    public File getConfigurationRoot() throws SiteWhereException {
	return ConfigurationResolver.getConfigurationFolder();
    }

    /**
     * Gets the CATALINA/conf/sitewhere folder where configs are stored.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static File getConfigurationFolder() throws SiteWhereException {
	String catalina = System.getProperty("sitewhere.home");
	if (catalina == null) {
	    throw new SiteWhereException("SiteWhere home environment variable not set.");
	}
	File catFolder = new File(catalina);
	if (!catFolder.exists()) {
	    throw new SiteWhereException("SiteWhere home folder does not exist.");
	}
	File confDir = new File(catalina, "conf");
	if (!confDir.exists()) {
	    throw new SiteWhereException("SiteWhere home conf folder does not exist.");
	}
	return confDir;
    }
}