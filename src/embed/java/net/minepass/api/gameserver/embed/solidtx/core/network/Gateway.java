/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import java.io.InputStream;

/**
 * Communication gateway for remote endpoints that accept
 * PayloadRequests containing entity requests/operations
 * and return Payloads containing new or updated data.
 *
 * Payloads (and Requests) are encoded/decoded through
 * the use of a NetworkAdapter, typically to/from raw
 * byte data.
 *
 * @see Payload
 * @see PayloadRequest
 * @see NetworkAdapter
 */
public abstract class Gateway {

    protected NetworkAdapter adapter;

    /**
     * Create new Gateway using a NetworkAdapter.
     *
     * @param adapter the network adapter
     */
    public Gateway(NetworkAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Get a payload from remote endpoint.
     *
     * @param request the payload request
     * @return the payload
     * @throws NetworkException on network failure
     */
    public Payload getPayload(PayloadRequest request) throws NetworkException {
        byte[] input = adapter.encodePayloadRequest(request);
        InputStream output = txEncodedPayloadRequest(input);
        return adapter.decodePayload(output);
    }

    protected abstract InputStream txEncodedPayloadRequest(byte[] input) throws NetworkException;

}
