/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.loadtest.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.loadtest.spi.agent.IAgentManager;
import com.sitewhere.loadtest.spi.agent.ILoadTestAgent;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IAgentManager}.
 * 
 * @author Derek
 */
public class AgentManager extends LifecycleComponent implements IAgentManager {

	/** Private logger instance */
	private static Logger LOGGER = Logger.getLogger(AgentManager.class);

	/** List of load test agents */
	private List<ILoadTestAgent<?>> agents = new ArrayList<ILoadTestAgent<?>>();

	/** Map of load test agents by id */
	private Map<String, ILoadTestAgent<?>> agentsById = new HashMap<String, ILoadTestAgent<?>>();

	public AgentManager() {
		super(LifecycleComponentType.Other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		for (ILoadTestAgent<?> agent : agents) {
			startNestedComponent(agent, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		for (ILoadTestAgent<?> agent : agents) {
			agent.lifecycleStop();
		}
	}

	/**
	 * Create map of agents by id.
	 * 
	 * @param agents
	 */
	protected void indexAgents(List<ILoadTestAgent<?>> agents) {
		agentsById.clear();
		for (ILoadTestAgent<?> agent : agents) {
			agentsById.put(agent.getAgentId(), agent);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.IAgentManager#getAgents()
	 */
	public List<ILoadTestAgent<?>> getAgents() {
		return agents;
	}

	public void setAgents(List<ILoadTestAgent<?>> agents) {
		this.agents = agents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.loadtest.spi.agent.IAgentManager#getAgentById(java.lang.String)
	 */
	@Override
	public ILoadTestAgent<?> getAgentById(String agentId) {
		return agentsById.get(agentId);
	}
}