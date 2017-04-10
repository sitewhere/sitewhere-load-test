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
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Base interface for components that produce events.
 * 
 * @author Derek
 */
public interface IEventProducer extends ILifecycleComponent {

    /**
     * Produce an event for the given recipient.
     * 
     * @param chooser
     * @return
     * @throws SiteWhereException
     */
    public IDecodedDeviceRequest<?> produceEvent(IDevice recipient) throws SiteWhereException;

    /**
     * Get amount of time in milliseconds to wait between producing messages.
     * Depending on the implementation class, this value may change over time to
     * affect the rate of event production.
     * 
     * @return
     * @throws SiteWhereException
     */
    public long getThrottleDelayInMs() throws SiteWhereException;
}