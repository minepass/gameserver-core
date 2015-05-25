/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import net.minepass.api.gameserver.embed.solidtx.core.router.ObjectRoute;

import java.util.ArrayList;

/**
 * Responsible for connecting Gateways to Payload Buses
 * to managing DataRequests and Payloads.
 */
public class NetworkManager {

    protected String stackVersion;
    protected ArrayList<PayloadBus> payloadBuses;
    protected ArrayList<Gateway> gateways;

    /**
     * Create new NetworkManager.
     */
    public NetworkManager(String stackVersion) {
        this.stackVersion = stackVersion;
        this.payloadBuses = new ArrayList<PayloadBus>(1);
        this.gateways = new ArrayList<Gateway>(1);
    }

    // Gateway
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Add Gateway to manager which corresponds
     * to a storage Container on the same channel.
     *
     * @see net.minepass.api.gameserver.embed.solidtx.core.storage.Container
     * @param gateway the gateway to add
     * @return the resulting channel
     */
    public int addGateway(Gateway gateway) {
        int channel = gateways.size();
        payloadBuses.add(new PayloadBus(gateway, channel));
        gateways.add(gateway);
        return channel;
    }

    public ArrayList<Gateway> getGateways() {
        return gateways;
    }

    // Payloads
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Get a payload bus from a channel id.
     *
     * @param channel the channel id
     * @return the payload bus or null
     */
    public PayloadBus getPayloadBus(int channel) {
        return payloadBuses.get(channel);
    }

    /**
     * Get a payload bus from an entity's route.
     *
     * @param route the entity's route
     * @return the payload bus or null
     */
    public PayloadBus getPayloadBus(ObjectRoute route) {
        return getPayloadBus(route.getChannel());
    }

    public ArrayList<PayloadBus> getPayloadBuses() {
        return payloadBuses;
    }

}
