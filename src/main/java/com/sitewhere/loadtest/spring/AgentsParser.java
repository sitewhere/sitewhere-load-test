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
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.loadtest.server.AgentManager;
import com.sitewhere.loadtest.spi.agent.IDeviceChooser;
import com.sitewhere.loadtest.spi.agent.devices.DevicePool;
import com.sitewhere.loadtest.spi.agent.mqtt.MqttAgent;

/**
 * Parses configuration information for the 'agents' section.
 * 
 * @author Derek
 */
public class AgentsParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(AgentManager.class);

		ManagedList<?> providers = parseAgents(element, context);
		manager.addPropertyValue("agents", providers);

		context.getRegistry().registerBeanDefinition(ILoadTestBeans.BEAN_AGENT_MANAGER,
				manager.getBeanDefinition());
		return null;
	}

	/**
	 * Parse the list of serach providers from nested elements.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected ManagedList<?> parseAgents(Element element, ParserContext context) {
		ManagedList<Object> result = new ManagedList<Object>();
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_LOADTEST_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException("Invalid nested element found in 'agents' section: "
							+ child.toString());
				}
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown agent element: " + child.getLocalName());
			}
			switch (type) {
			case MqttAgent: {
				result.add(parseMqttAgentConfiguration(child, context));
				break;
			}
			}
		}
		return result;
	}

	/**
	 * Parse the global Hazelcast configuration.
	 * 
	 * @param element
	 * @param context
	 */
	protected AbstractBeanDefinition parseMqttAgentConfiguration(Element element, ParserContext context) {
		BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(MqttAgent.class);

		// Handle common agent fields.
		handleCommonAgentData(element, config);

		return config.getBeanDefinition();
	}

	/**
	 * Handle information common to all agents.
	 * 
	 * @param element
	 * @param config
	 */
	protected void handleCommonAgentData(Element element, BeanDefinitionBuilder config) {
		Attr agentId = element.getAttributeNode("agentId");
		if (agentId == null) {
			throw new RuntimeException("Agent configuration missing 'agentId' attribute.");
		}
		config.addPropertyValue("agentId", agentId.getValue());

		Attr numThreads = element.getAttributeNode("numThreads");
		if (numThreads == null) {
			throw new RuntimeException("Agent configuration missing 'numThreads' attribute.");
		}
		config.addPropertyValue("numThreads", numThreads.getValue());

		// Configure a device chooser.
		configureDeviceChooser(element, config);
	}

	/**
	 * Configure an {@link IDeviceChooser} for the agent.
	 * 
	 * @param element
	 * @param config
	 */
	protected void configureDeviceChooser(Element element, BeanDefinitionBuilder agent) {
		List<Element> children = DomUtils.getChildElements(element);
		AbstractBeanDefinition chooser = null;
		for (Element child : children) {
			DeviceChoosers type = DeviceChoosers.getByLocalName(child.getLocalName());
			if (type != null) {
				switch (type) {
				case DevicePool: {
					chooser = parseDevicePool(child);
					break;
				}
				}
			}
		}
		if (chooser == null) {
			throw new RuntimeException("No device chooser configured for agent.");
		}
		agent.addPropertyValue("deviceChooser", chooser);
	}

	protected AbstractBeanDefinition parseDevicePool(Element element) {
		BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(DevicePool.class);

		Attr poolSize = element.getAttributeNode("poolSize");
		if (poolSize == null) {
			throw new RuntimeException("Device pool configuration missing 'poolSize' attribute.");
		}
		config.addPropertyValue("poolSize", poolSize.getValue());

		return config.getBeanDefinition();
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** MQTT Agent */
		MqttAgent("mqtt-agent");

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

	/**
	 * Allowed values for device choosers.
	 * 
	 * @author Derek
	 */
	public static enum DeviceChoosers {

		/** Device Pool */
		DevicePool("device-pool");

		/** Event code */
		private String localName;

		private DeviceChoosers(String localName) {
			this.localName = localName;
		}

		public static DeviceChoosers getByLocalName(String localName) {
			for (DeviceChoosers value : DeviceChoosers.values()) {
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
