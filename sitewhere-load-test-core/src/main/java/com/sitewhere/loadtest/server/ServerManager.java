package com.sitewhere.loadtest.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.loadtest.spi.server.IServerManager;
import com.sitewhere.loadtest.spi.server.ISiteWhereConnection;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IServerManager} that holds a list of SiteWhere
 * server connection data.
 * 
 * @author Derek
 */
public class ServerManager extends LifecycleComponent implements IServerManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** SiteWhere connections indexed by server id */
    private Map<String, ISiteWhereConnection> connectionsByServerId = new HashMap<String, ISiteWhereConnection>();

    public ServerManager() {
	super(LifecycleComponentType.Other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	LOGGER.info("Starting server manager with " + getConnectionsByServerId().size() + " servers.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.loadtest.spi.server.IServerManager#getConnectionsByServerId
     * ()
     */
    @Override
    public Map<String, ISiteWhereConnection> getConnectionsByServerId() {
	return connectionsByServerId;
    }

    public void setConnectionsByServerId(Map<String, ISiteWhereConnection> connectionsByServerId) {
	this.connectionsByServerId = connectionsByServerId;
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
}