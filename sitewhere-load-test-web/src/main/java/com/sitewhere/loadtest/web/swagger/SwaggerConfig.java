/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.web.swagger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.sitewhere.loadtest.version.VersionHelper;

/**
 * Configures information used to generate Swagger metadata.
 * 
 * @author Derek
 */
@Configuration
@EnableSwagger
@EnableWebMvc
public class SwaggerConfig {

    /** Title for API page */
    private static final String API_TITLE = "Load Test REST APIs";

    /** Description for API page */
    private static final String API_DESCRIPTION = "Operations that allow remote clients to interact with SiteWhere Load Test framework.";

    /** Contact email for API questions */
    private static final String API_CONTACT_EMAIL = "derek.adams@sitewhere.com";

    /** License type information */
    private static final String API_LICENSE_TYPE = "Commercial License";

    /** Contact email for API questions */
    private static final String API_LICENSE_URL = "http://www.sitewhere.com";

    /** Swagger configuration */
    private SpringSwaggerConfig springSwaggerConfig;

    /** SiteWhere path provider */
    private PathProvider pathProvider;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
	this.springSwaggerConfig = springSwaggerConfig;
    }

    @Autowired
    public void setServletContext(ServletContext servletContext) {
	this.pathProvider = new PathProvider(servletContext);
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
	ApiInfo apiInfo = new ApiInfo(API_TITLE, API_DESCRIPTION, null, API_CONTACT_EMAIL, API_LICENSE_TYPE,
		API_LICENSE_URL);
	return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).pathProvider(pathProvider).apiInfo(apiInfo)
		.apiVersion(VersionHelper.getVersion().getVersionIdentifier());
    }
}