/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sitewhere.loadtest.server.SiteWhereConnection;

/**
 * Parses connection section of load test node configuration file.
 * 
 * @author Derek
 */
public class ConnectionParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(SiteWhereConnection.class);

		NodeList list =
				element.getElementsByTagNameNS(IConfigurationElements.SITEWHERE_LOADTEST_NS,
						"sitewhere-api-url");
		if (list.getLength() == 0) {
			throw new RuntimeException("Configuration missing 'sitewhere-api-url' value.");
		}
		manager.addPropertyValue("siteWhereApiUrl", list.item(0).getTextContent());

		context.getRegistry().registerBeanDefinition(ILoadTestBeans.BEAN_SITEWHERE_CONNECTION,
				manager.getBeanDefinition());
		return null;
	}
}
