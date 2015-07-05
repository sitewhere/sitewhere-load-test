/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventEncoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ILoadTestAgent} that sends traffic via an MQTT broker.
 * 
 * @author Derek
 */
public abstract class Agent<T> extends LifecycleComponent implements ILoadTestAgent<T> {

	/** Default number of processing threads */
	public static final int DEFAULT_NUM_THREADS = 1;

	/** Unique agent id */
	private String agentId;

	/** Number of threads for processing */
	private int numThreads = DEFAULT_NUM_THREADS;

	/** Device chooser implementation */
	private IDeviceChooser deviceChooser;

	/** Event producer implementation */
	private IEventProducer eventProducer;

	/** Event encoder implementation */
	private IDeviceEventEncoder<T> eventEncoder;

	/** Used to execute processing in separate threads */
	private ExecutorService executor;

	public Agent() {
		super(LifecycleComponentType.Other);
	}

	/**
	 * Process a single event.
	 * 
	 * @throws SiteWhereException
	 */
	public void sendEvent() throws SiteWhereException {
		IDevice recipient = getDeviceChooser().chooseDevice();
		IDecodedDeviceRequest<?> request = getEventProducer().produceEvent(recipient);
		T encoded = getEventEncoder().encode(request);
		deliver(recipient, encoded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Start device chooser as nested component.
		startNestedComponent(getDeviceChooser(), true);

		executor = Executors.newFixedThreadPool(getNumThreads());
		for (int i = 0; i < getNumThreads(); i++) {
			executor.submit(new AgentDeliveryThread());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		executor.shutdownNow();
	}

	/**
	 * Thread that delivers commands to SiteWhere.
	 * 
	 * @author Derek
	 */
	protected class AgentDeliveryThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					sendEvent();
				} catch (SiteWhereException e) {
					getLogger().error("Unable to send event.", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.ILoadTestAgent#getAgentId()
	 */
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.ILoadTestAgent#getDeviceChooser()
	 */
	public IDeviceChooser getDeviceChooser() {
		return deviceChooser;
	}

	public void setDeviceChooser(IDeviceChooser deviceChooser) {
		this.deviceChooser = deviceChooser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.ILoadTestAgent#getEventProducer()
	 */
	public IEventProducer getEventProducer() {
		return eventProducer;
	}

	public void setEventProducer(IEventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}

	public IDeviceEventEncoder<T> getEventEncoder() {
		return eventEncoder;
	}

	public void setEventEncoder(IDeviceEventEncoder<T> eventEncoder) {
		this.eventEncoder = eventEncoder;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
}