/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import net.minepass.api.gameserver.embed.solidtx.core.data.DataException;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Decoded representation of a Payload request, which will
 * be encoded through a NetworkAdapter and transmitted via
 * a Gateway, resulting in a Payload.
 *
 * Each request contains a set of DataRequests regarding
 * newly needed entities or entity updates, as well as
 * a StorageIndex to poll for changes to existing entities
 * and reflect the last set of attributes provided by the
 * Gateway (if applicable).
 *
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.Payload
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.DataRequest
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.Gateway
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.NetworkAdapter
 */
public class PayloadRequest {

    protected int attempts;
    protected Exception lastError;
    protected List<DataRequest> dataRequests;
    protected StorageIndex storageIndex;

    /**
     * Create new PayloadRequest from a DataRequest
     * queue and Container StorageIndex.
     *
     * WARNING: The input request queue will be altered
     *          and the accepted requests will remain
     *          only within this payload. Requests
     *          are pulled in FIFO order.
     *
     * @param readFromQueue the data request queue to read from (see WARNING)
     * @param storageIndex the container storage index
     */
    public PayloadRequest(Queue<DataRequest> readFromQueue, StorageIndex storageIndex) {
        this.attempts = 0;
        this.dataRequests = new ArrayList<DataRequest>();
        this.storageIndex = storageIndex;

        DataRequest x;
        while ((x = readFromQueue.poll()) != null) {
            dataRequests.add(x);
        }
    }

    public int getAttempts() {
        return attempts;
    }

    public Exception getLastError() {
        return lastError;
    }

    public List<DataRequest> getDataRequests() {
        return dataRequests;
    }

    public StorageIndex getStorageIndex() {
        return storageIndex;
    }

    protected void respondWithPayload(Payload payload) {
        for (DataRequest x : dataRequests) {
            Map<String,Object> txoData = payload.getUpdates().get(x.getRoute().getDataGroup(), x.getObjectId());
            if (txoData != null) {
                x.setObjectData(txoData);
            } else {
                // Search for error.

                // Otherwise.
                x.setError(new DataException("Unknown error."));
            }
        }
    }
}
