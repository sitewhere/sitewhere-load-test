/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent.devices;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.loadtest.LoadTest;
import com.sitewhere.loadtest.spi.agent.IDeviceChooser;
import com.sitewhere.loadtest.spi.server.ISiteWhereConnection;
import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.DeviceSearchResults;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceChooser} that chooses from a pool of devices.
 * 
 * @author Derek
 */
public class DevicePool extends LifecycleComponent implements IDeviceChooser {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(DevicePool.class);

	/** Amount of time to wait for REST connection */
	private static final int REST_CONNECTION_TIMEOUT = 2000;

	/** Size of device pool */
	private int poolSize = 0;

	/** Pool of devices to choose from */
	private List<Device> devicePool = new ArrayList<Device>();

	public DevicePool() {
		super(LifecycleComponentType.Other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		ISiteWhereConnection connection = LoadTest.getLoadTestServer().getSiteWhereConnection();
		ISiteWhereClient client =
				new SiteWhereClient(connection.getRestApiUrl(), connection.getRestUsername(),
						connection.getRestPassword(), REST_CONNECTION_TIMEOUT);
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, getPoolSize(), null, null);
		DeviceSearchResults matches = client.listDevices(false, false, true, true, criteria);
		this.devicePool = matches.getResults();
		LOGGER.info("Created device pool with " + devicePool.size() + " devices.");
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
	 * @see com.sitewhere.loadtest.spi.agent.IDeviceChooser#chooseDevice()
	 */
	@Override
	public IDevice chooseDevice() throws SiteWhereException {
		int slot = (int) ((Math.random()) * devicePool.size());
		return devicePool.get(slot);
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

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
}