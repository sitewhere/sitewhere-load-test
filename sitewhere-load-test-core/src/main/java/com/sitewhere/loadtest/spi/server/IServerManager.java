package com.sitewhere.loadtest.spi.server;

import java.util.Map;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages list of servers the load test node will interact with.
 * 
 * @author Derek
 */
public interface IServerManager extends ILifecycleComponent {

    /**
     * Get map of server connections by unique server id.
     * 
     * @return
     */
    public Map<String, ISiteWhereConnection> getConnectionsByServerId();
}