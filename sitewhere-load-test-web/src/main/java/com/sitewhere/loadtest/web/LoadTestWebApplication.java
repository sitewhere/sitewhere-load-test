/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.web;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.sitewhere.loadtest.LoadTestApplication;
import com.sitewhere.loadtest.web.rest.RestMvcConfiguration;
import com.sitewhere.loadtest.web.swagger.SwaggerConfig;

/**
 * Spring Boot application that loads load tester with embedded Tomcat container
 * and REST services.
 * 
 * @author Derek
 */
@Configuration
public class LoadTestWebApplication extends LoadTestApplication {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
	TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
	    protected void postProcessContext(Context context) {
		final int cacheSize = 100 * 1024 * 1024;
		StandardRoot standardRoot = new StandardRoot(context);
		standardRoot.setCacheMaxSize(cacheSize);
		standardRoot.setCacheObjectMaxSize(Integer.MAX_VALUE / 1024);
		context.setResources(standardRoot);
	    }
	};
	tomcat.setContextPath("/loadtest");
	return tomcat;
    }

    @Bean
    public ServletRegistrationBean sitewhereRestInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestMvcConfiguration.class, SwaggerConfig.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		RestMvcConfiguration.REST_API_MATCHER);
	registration.setName("loadTestRestInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public CorsFilter corsFilter() {
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	CorsConfiguration config = new CorsConfiguration();
	config.addAllowedOrigin("*");
	config.addAllowedHeader("*");
	config.addAllowedMethod("*");
	config.addExposedHeader("Authorization");
	config.addExposedHeader("Content-Type");
	source.registerCorsConfiguration("/api/**", config);
	return new CorsFilter(source);
    }

    /**
     * Acts on shutdown hook to gracefully shut down SiteWhere server
     * components.
     * 
     * @return
     */
    @Bean
    public ShutdownListener shutdownListener() {
	return new ShutdownListener();
    }

    public static void main(String[] args) {
	SpringApplication.run(LoadTestWebApplication.class, args);
    }
}