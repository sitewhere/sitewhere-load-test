/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.agent.mqtt;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.core.DataUtils;
import com.sitewhere.loadtest.agent.Agent;
import com.sitewhere.loadtest.spi.agent.ILoadTestAgent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;

/**
 * Implementation of {@link ILoadTestAgent} that sends traffic via an MQTT broker.
 * 
 * @author Derek
 */
public class MqttAgent extends Agent<byte[]> {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttAgent.class);

	/** Default hostname if not set via Spring */
	public static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port if not set from Spring */
	public static final int DEFAULT_PORT = 1883;

	/** Default topic name if not set via Spring */
	public static final String DEFAULT_TOPIC_NAME = "SiteWhere/input/protobuf";

	/** Default connection timeout in seconds */
	public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** MQTT topic name */
	private String topicName = DEFAULT_TOPIC_NAME;

	/** MQTT client */
	private MQTT mqtt;

	/** Shared MQTT connection */
	private FutureConnection connection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.mqtt = new MQTT();
		try {
			mqtt.setHost(getHostname(), getPort());
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
		LOGGER.info("Connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
		connection = mqtt.futureConnection();
		try {
			Future<Void> future = connection.connect();
			future.await(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new SiteWhereException("Unable to connect to MQTT broker.", e);
		}
		LOGGER.info("Connected to MQTT broker.");

		// Required for starting threads.
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		// Required for shutting down threads.
		super.stop();

		if (connection != null) {
			Future<Void> shutdown = connection.disconnect();
			try {
				shutdown.await(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new SiteWhereException("Unable to disconnect from MQTT broker.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.loadtest.spi.agent.ILoadTestAgent#deliver(com.sitewhere.spi.device
	 * .IDevice, java.lang.Object)
	 */
	@Override
	public void deliver(IDevice device, byte[] payload) throws SiteWhereException {
		try {
			getLogger().debug(
					"Sending encoded message to topic '" + getTopicName() + "': "
							+ DataUtils.bytesToHex(payload));
			connection.publish(getTopicName(), payload, QoS.AT_LEAST_ONCE, false);
		} catch (Exception e) {
			throw new SiteWhereException("Unable to deliver event to MQTT topic.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
}