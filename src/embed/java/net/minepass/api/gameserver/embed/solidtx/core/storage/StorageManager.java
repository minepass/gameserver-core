/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.storage;

import net.minepass.api.gameserver.embed.solidtx.core.network.Gateway;
import net.minepass.api.gameserver.embed.solidtx.core.network.Payload;
import net.minepass.api.gameserver.embed.solidtx.core.router.ObjectRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for connecting DataStores and Containers,
 * and processing network payloads for storage updates.
 *
 * @see DataStore
 * @see Container
 */
public class StorageManager {

    protected String stackVersion;
    protected List<DataStore> dataStores;

    /**
     * Create new StorageManager.
     */
    public StorageManager(String stackVersion) {
        this.stackVersion = stackVersion;
        this.dataStores = new ArrayList<DataStore>(1);
    }

    /**
     * Add container to manager which corresponds
     * to a network Gateway on the same channel.
     *
     * @see Gateway
     * @param container the container to add
     * @return the resulting channel
     */
    public int addContainer(Container container) {
        dataStores.add(new DataStore(stackVersion, container));
        return dataStores.size()-1;
    }

    /**
     * Get a data store from a channel id.
     *
     * @param channel the channel id
     * @return the data store or null
     * @throws java.lang.RuntimeException on undefined channel
     */
    public DataStore getDataStore(int channel) {
        if (dataStores.size() <= channel) {
            throw new RuntimeException("Undefined storage channel.");
        }
        return dataStores.get(channel);
    }

    /**
     * Get a data store from an entity's route.
     *
     * @param route the entity's route
     * @return the data store or null
     */
    public DataStore getDataStore(ObjectRoute route) {
        return getDataStore(route.getChannel());
    }

    public List<DataStore> getDataStores() {
        return dataStores;
    }

    /**
     * Receive data updates from a network payload and
     * merge them into the data store and container.
     *
     * @param channel the channel id
     * @param payload the payload to receive
     */
    public void processPayload(int channel, Payload payload) {
        DataStore ds = getDataStore(channel);
        ds.mergeWith(payload.getUpdates());
        ds.setAttribute("meta", payload.getMeta());
        ds.flush();
    }


}
