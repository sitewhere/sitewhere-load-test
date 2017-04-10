/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.spi.agent;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages the list of agents configured to perform load testing.
 * 
 * @author Derek
 */
public interface IAgentManager extends ILifecycleComponent {

    /**
     * Get list of all agents.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<ILoadTestAgent<?>> getAgents() throws SiteWhereException;

    /**
     * Get agent by unique id.
     * 
     * @param agentId
     * @return
     * @throws SiteWhereException
     */
    public ILoadTestAgent<?> getAgentById(String agentId) throws SiteWhereException;
}