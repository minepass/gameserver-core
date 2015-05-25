/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.router;

import net.minepass.api.gameserver.embed.solidtx.TxException;
import net.minepass.api.gameserver.embed.solidtx.core.network.Gateway;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for binding entity classes to a specific
 * network Gateway, data group, and channel.
 */
public class Router {

    protected Map<Class,ObjectRoute> classMap;

    /**
     * Create new Router.
     */
    public Router() {
        classMap = new HashMap<Class, ObjectRoute>();
    }

    /**
     * Add route binding entity class to a Gateway,
     * data group, and channel.
     *
     * @param objectClass the entity class
     * @param gateway the entity's network gateway
     * @param dataGroup the entity's data group on the gateway
     * @param channel the channel id used to link the route to other stack services
     * @throws TxException on duplicated entity classes
     */
    public void addRoute(Class objectClass, Gateway gateway, String dataGroup, int channel) throws TxException {
        if (classMap.containsKey(objectClass)) {
            throw new TxException("Cannot add multiple gateway routes for a class.");
        }
        ObjectRoute route = new ObjectRoute(objectClass, gateway, dataGroup, channel);
        classMap.put(objectClass, route);
    }

    /**
     * Get route associated with an entity class.
     *
     * @param objectClass the entity class
     * @return the entity's route
     * @throws TxException when no matching route exists
     */
    public ObjectRoute getRoute(Class objectClass) throws MissingRouteException {
        if ( ! classMap.containsKey(objectClass)) {
            throw new MissingRouteException(objectClass);
        }

        return classMap.get(objectClass);
    }

}
