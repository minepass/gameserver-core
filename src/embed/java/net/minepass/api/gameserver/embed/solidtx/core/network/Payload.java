/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import net.minepass.api.gameserver.embed.solidtx.core.data.DataSet;

/**
 * Decoded representation of a network data payload,
 * produced via a Gateway and NetworkAdapter.
 *
 * Each payload contains a DataSet of entity updates,
 * along with a series of attributes that should be
 * stored on a Gateway basis and sent along with the
 * next PayloadRequest.
 *
 * @see PayloadRequest
 * @see Gateway
 * @see NetworkAdapter
 */
public class Payload {

    protected String meta;
    protected DataSet updates;

    /**
     * Create new Payload from decoded attributes and data.
     *
     * @param updates the set of entity updates
     * @param meta the payload meta string
     */
    public Payload(DataSet updates, String meta) {
        this.meta = meta;
        this.updates = updates;
    }

    public String getMeta() {
        return meta;
    }

    public DataSet getUpdates() {
        return updates;
    }

}
