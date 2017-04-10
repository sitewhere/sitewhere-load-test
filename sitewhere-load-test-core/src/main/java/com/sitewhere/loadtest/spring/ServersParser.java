/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.loadtest.server.ServerManager;
import com.sitewhere.loadtest.server.SiteWhereConnection;

/**
 * Parses servers section of load test node configuration file.
 * 
 * @author Derek
 */
public class ServersParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	ManagedMap<String, AbstractBeanDefinition> servers = new ManagedMap<String, AbstractBeanDefinition>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown connection element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SiteWhereServer: {
		parseSiteWhereConfiguration(child, context, servers);
		break;
	    }
	    }
	}

	BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(ServerManager.class);
	config.addPropertyValue("connectionsByServerId", servers);
	context.getRegistry().registerBeanDefinition(ILoadTestBeans.BEAN_SERVER_MANAGER, config.getBeanDefinition());
	return null;
    }

    /**
     * Parse a SiteWhere server reference.
     * 
     * @param element
     * @param context
     * @param servers
     */
    protected void parseSiteWhereConfiguration(Element element, ParserContext context,
	    ManagedMap<String, AbstractBeanDefinition> servers) {
	BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(SiteWhereConnection.class);

	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("SiteWhere configuration missing 'id' attribute.");
	}
	String serverId = id.getValue();

	Attr restApiUrl = element.getAttributeNode("restApiUrl");
	if (restApiUrl == null) {
	    throw new RuntimeException("SiteWhere configuration missing 'restApiUrl' attribute.");
	}
	config.addPropertyValue("restApiUrl", restApiUrl.getValue());

	Attr restApiUsername = element.getAttributeNode("restApiUsername");
	if (restApiUsername == null) {
	    throw new RuntimeException("SiteWhere configuration missing 'restApiUsername' attribute.");
	}
	config.addPropertyValue("restUsername", restApiUsername.getValue());

	Attr restApiPassword = element.getAttributeNode("restApiPassword");
	if (restApiPassword == null) {
	    throw new RuntimeException("SiteWhere configuration missing 'restApiPassword' attribute.");
	}
	config.addPropertyValue("restPassword", restApiPassword.getValue());

	servers.put(serverId, config.getBeanDefinition());
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** SiteWhere server configuration */
	SiteWhereServer("sitewhere-server");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}
