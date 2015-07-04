/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent.producer;

import org.apache.log4j.Logger;

import com.sitewhere.loadtest.spi.agent.IEventProducer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for event producer implementations.
 * 
 * @author Derek
 */
public class EventProducer extends LifecycleComponent implements IEventProducer {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(EventProducer.class);

	/** Weight value determining number of alerts relative to other events */
	private int alertRatio = 1;

	/** Weight value determining number of locations relative to other events */
	private int locationRatio = 1;

	/** Weight value determining number of measurements relative to other events */
	private int measurementsRatio = 1;

	/** Throttle delay in milliseconds */
	private long throttleDelayInMs = 0;

	/** Calculated cutoff value between 0 and 1 where a location should be generated */
	private double alertCutoff;

	/** Calculated cutoff value between 0 and 1 where measurements should be generated */
	private double locationCutoff;

	public EventProducer() {
		super(LifecycleComponentType.Other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		double total = alertRatio + locationRatio + measurementsRatio;
		alertCutoff = ((double) alertRatio) / total;
		locationCutoff = alertCutoff + ((double) locationRatio) / total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.loadtest.spi.agent.IEventProducer#produceEvent(com.sitewhere.spi.
	 * device.IDevice)
	 */
	@Override
	public IDecodedDeviceRequest<?> produceEvent(IDevice recipient) throws SiteWhereException {
		try {
			double rand = Math.random();
			if (rand <= alertCutoff) {
				return produceAlert(recipient);
			} else if (rand <= locationCutoff) {
				return produceLocation(recipient);
			}
			return produceMeasurements(recipient);
		} finally {
			waitForThrottleDelay();
		}
	}

	/**
	 * Wait for the throttle delay to expire.
	 */
	protected void waitForThrottleDelay() {
		if (getThrottleDelayInMs() > 0) {
			try {
				Thread.sleep(getThrottleDelayInMs());
			} catch (InterruptedException e) {
				LOGGER.warn("Event producer interrupted during throttle delay.");
			}
		}
	}

	/**
	 * Produce an alert event for the recipient.
	 * 
	 * @param recipient
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDecodedDeviceRequest<IDeviceAlertCreateRequest> produceAlert(IDevice recipient)
			throws SiteWhereException {
		return null;
	}

	/**
	 * Produce a location event for the recipient.
	 * 
	 * @param recipient
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDecodedDeviceRequest<IDeviceLocationCreateRequest> produceLocation(IDevice recipient)
			throws SiteWhereException {
		return null;
	}

	/**
	 * Produce a measurements event for the recipient.
	 * 
	 * @param recipient
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest> produceMeasurements(IDevice recipient)
			throws SiteWhereException {
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.IEventProducer#getThrottleDelayInMs()
	 */
	public long getThrottleDelayInMs() {
		return throttleDelayInMs;
	}

	public void setThrottleDelayInMs(long throttleDelayInMs) {
		this.throttleDelayInMs = throttleDelayInMs;
	}

	public int getAlertRatio() {
		return alertRatio;
	}

	public void setAlertRatio(int alertRatio) {
		this.alertRatio = alertRatio;
	}

	public int getLocationRatio() {
		return locationRatio;
	}

	public void setLocationRatio(int locationRatio) {
		this.locationRatio = locationRatio;
	}

	public int getMeasurementsRatio() {
		return measurementsRatio;
	}

	public void setMeasurementsRatio(int measurementsRatio) {
		this.measurementsRatio = measurementsRatio;
	}
}