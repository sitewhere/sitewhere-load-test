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

import com.sitewhere.device.communication.protobuf.ProtobufDeviceEventEncoder;
import com.sitewhere.loadtest.agent.devices.DevicePool;
import com.sitewhere.loadtest.agent.mqtt.MqttAgent;
import com.sitewhere.loadtest.agent.producer.EventProducer;
import com.sitewhere.loadtest.server.AgentManager;
import com.sitewhere.loadtest.spi.agent.IDeviceChooser;
import com.sitewhere.loadtest.spi.agent.IEventProducer;
import com.sitewhere.spi.device.communication.IDeviceEventEncoder;

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

		// Configure a binary encoder.
		configureBinaryEncoder(element, config);

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

		// Configure an event producer.
		configureEventProducer(element, config);
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

	/**
	 * Parse configuration for device pool.
	 * 
	 * @param element
	 * @return
	 */
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
	 * Configure an {@link IEventProducer} for the agent.
	 * 
	 * @param element
	 * @param config
	 */
	protected void configureEventProducer(Element element, BeanDefinitionBuilder agent) {
		List<Element> children = DomUtils.getChildElements(element);
		AbstractBeanDefinition producer = null;
		for (Element child : children) {
			EventProducers type = EventProducers.getByLocalName(child.getLocalName());
			if (type != null) {
				switch (type) {
				case LinearEventProducer: {
					producer = parseLinearEventProducer(child);
					break;
				}
				}
			}
		}
		if (producer == null) {
			throw new RuntimeException("No event producer configured for agent.");
		}
		agent.addPropertyValue("eventProducer", producer);
	}

	/**
	 * Parse configuration for a linear event producer.
	 * 
	 * @param element
	 * @return
	 */
	protected AbstractBeanDefinition parseLinearEventProducer(Element element) {
		BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(EventProducer.class);

		Attr throttleDelayMs = element.getAttributeNode("throttleDelayMs");
		if (throttleDelayMs != null) {
			config.addPropertyValue("throttleDelayInMs", throttleDelayMs.getValue());
		}

		return config.getBeanDefinition();
	}

	/**
	 * Configure an {@link IDeviceEventEncoder} that produces binary payloads.
	 * 
	 * @param element
	 * @param config
	 */
	protected void configureBinaryEncoder(Element element, BeanDefinitionBuilder agent) {
		List<Element> children = DomUtils.getChildElements(element);
		AbstractBeanDefinition encoder = null;
		for (Element child : children) {
			BinaryEncoders type = BinaryEncoders.getByLocalName(child.getLocalName());
			if (type != null) {
				switch (type) {
				case ProtobufEncoder: {
					encoder = parseProtobufEventEncoder(child);
					break;
				}
				}
			}
		}
		if (encoder == null) {
			throw new RuntimeException("No event encoder configured for agent.");
		}
		agent.addPropertyValue("eventEncoder", encoder);
	}

	/**
	 * Parse configuration for a protobuf event encoder.
	 * 
	 * @param element
	 * @return
	 */
	protected AbstractBeanDefinition parseProtobufEventEncoder(Element element) {
		BeanDefinitionBuilder config =
				BeanDefinitionBuilder.rootBeanDefinition(ProtobufDeviceEventEncoder.class);
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

	/**
	 * Allowed values for event producers.
	 * 
	 * @author Derek
	 */
	public static enum EventProducers {

		/** Linear event producer */
		LinearEventProducer("linear-event-producer");

		/** Event code */
		private String localName;

		private EventProducers(String localName) {
			this.localName = localName;
		}

		public static EventProducers getByLocalName(String localName) {
			for (EventProducers value : EventProducers.values()) {
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
	 * Allowed values for binary event encoders.
	 * 
	 * @author Derek
	 */
	public static enum BinaryEncoders {

		/** SiteWhere Google Protocol Buffer encoder */
		ProtobufEncoder("protobuf-encoder");

		/** Event code */
		private String localName;

		private BinaryEncoders(String localName) {
			this.localName = localName;
		}

		public static BinaryEncoders getByLocalName(String localName) {
			for (BinaryEncoders value : BinaryEncoders.values()) {
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