/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.router;

import net.minepass.api.gameserver.embed.solidtx.core.network.Gateway;

/**
 * Binding between and entity class, network Gateway, data group, and channel.
 */
public class ObjectRoute {

    private Class objectClass;
    private Gateway gateway;
    private String dataGroup;
    private int channel;

    /**
     * Should only be created by Router.
     * @see Router#addRoute(Class, Gateway, String, int)
     */
    protected ObjectRoute(Class objectClass, Gateway gateway, String dataGroup, int channel) {
        this.objectClass = objectClass;
        this.gateway = gateway;
        this.dataGroup = dataGroup;
        this.channel = channel;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public String getDataGroup() {
        return dataGroup;
    }

    public int getChannel() {
        return channel;
    }
}
