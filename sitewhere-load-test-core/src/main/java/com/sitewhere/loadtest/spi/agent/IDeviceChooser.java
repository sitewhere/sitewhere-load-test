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
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Interface for component that chooses which device events will be submitted
 * to.
 * 
 * @author Derek
 */
public interface IDeviceChooser extends ILifecycleComponent {

    /**
     * Choose a device.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IDevice chooseDevice() throws SiteWhereException;
}