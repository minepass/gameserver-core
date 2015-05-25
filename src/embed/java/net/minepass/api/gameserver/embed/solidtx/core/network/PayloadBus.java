/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageIndex;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Links network Gateway to a set of DataRequests in order
 * to request/fetch the next Payload.
 *
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.Gateway
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.DataRequest
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.PayloadRequest
 * @see net.minepass.api.gameserver.embed.solidtx.core.network.Payload
 */
public class PayloadBus {

    protected Gateway gateway;
    protected int channel;
    protected ConcurrentLinkedQueue<DataRequest> dataRequests;  // non-blocking, fifo
    protected PayloadRequest pendingPayloadRequest;

    /**
     * Create new PayloadBus for a specific gateway,
     * on a channel id used to align it with other
     * stack services.
     *
     * @param gateway the network gateway
     * @param channel the channel id
     */
    public PayloadBus(Gateway gateway, int channel) {
        this.gateway = gateway;
        this.channel = channel;
        this.dataRequests = new ConcurrentLinkedQueue<DataRequest>();
    }

    public Gateway getGateway() {
        return gateway;
    }

    public int getChannel() {
        return channel;
    }

    /**
     * Add data request for the next payload.
     *
     * @param request the request to add
     */
    public void addDataRequest(DataRequest request) {
        dataRequests.add(request);
    }

    /**
     * Get the next payload responding to all new data requests,
     * as well as requesting updates to existing stored entities.
     *
     * If the last payload was not received successfully then the
     * previous payload will be requested again and existing data
     * requests will remain on the bus.
     *
     * WARNING: This network operation occurs synchronously on the
     *          gateway and will block the thread.
     *
     * @param storageIndex the index the storage container on this channel
     * @return the payload
     * @throws NetworkException on network error
     */
    public Payload getNextPayload(StorageIndex storageIndex) throws NetworkException {
        if (pendingPayloadRequest == null) {
            // Request queue will be emptied into new request.
            pendingPayloadRequest = new PayloadRequest(dataRequests, storageIndex);
        }

        Payload p;
        try {
            p = getGateway().getPayload(pendingPayloadRequest);
        } catch (NetworkException e) {
            pendingPayloadRequest.attempts++;
            pendingPayloadRequest.lastError = e;
            throw e;
        }

        pendingPayloadRequest.respondWithPayload(p);
        pendingPayloadRequest = null;
        return p;
    }

}
