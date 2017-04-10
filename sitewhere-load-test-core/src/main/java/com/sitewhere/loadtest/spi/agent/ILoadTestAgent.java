/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.communication.IDeviceEventEncoder;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Common interface implemented by all load test agents.
 * 
 * @author Derek
 */
public interface ILoadTestAgent<T> extends ILifecycleComponent {

    /**
     * Get unique agent id.
     * 
     * @return
     */
    public String getAgentId();

    /**
     * Get component that determines which devices will be sent events.
     * 
     * @return
     */
    public IDeviceChooser getDeviceChooser();

    /**
     * Get the event producer implementation.
     * 
     * @return
     */
    public IEventProducer getEventProducer();

    /**
     * Get the event encoder implementation.
     * 
     * @return
     */
    public IDeviceEventEncoder<T> getEventEncoder();

    /**
     * Deliver payload to SiteWhere.
     * 
     * @param device
     * @param payload
     * @throws SiteWhereException
     */
    public void deliver(IDevice device, T payload) throws SiteWhereException;
}