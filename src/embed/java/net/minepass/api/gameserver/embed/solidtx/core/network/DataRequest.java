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
import net.minepass.api.gameserver.embed.solidtx.core.object.ObjectState;
import net.minepass.api.gameserver.embed.solidtx.core.router.ObjectRoute;

import java.util.Map;

/**
 * A meta object used to track the status of a
 * network request for entity data.
 */
public class DataRequest {

    public enum Action { PULL, PUSH }
    public enum Status { PENDING, FOUND, ERROR }

    protected ObjectRoute route;
    protected Comparable objectId;
    protected Map<String,Object> objectData;
    protected Action action;
    protected Status status;
    protected DataException error;

    /**
     * Create new DataRequest.
     *
     * @param route the entity's route
     * @param id the entity id
     */
    public DataRequest(ObjectRoute route, Comparable id) {
        this.route = route;
        this.objectId = id;
        this.action = Action.PULL;
        this.status = Status.PENDING;
    }

    public DataRequest(ObjectRoute route, ObjectState state) {
        this.route = route;
        this.objectId = state.getObjectId();
        this.objectData = state.getObjectData();
        this.action = Action.PUSH;
        this.status = Status.PENDING;
    }

    /**
     * @return the entity's route
     */
    public ObjectRoute getRoute() {
        return route;
    }

    /**
     * @return the entity id
     */
    public Comparable getObjectId() {
        return objectId;
    }

    /**
     * @return the entity class
     */
    public Class getObjectClass() {
        return getRoute().getObjectClass();
    }

    /**
     * @return the received entity data
     */
    public Map<String, Object> getObjectData() {
        return objectData;
    }

    public void setObjectData(Map<String, Object> objectData) {
        if (objectData == null) {
            throw new RuntimeException("DataRequest object data cannot be set to null.");
        }
        this.objectData = objectData;
        this.status = Status.FOUND;
    }

    public Action getAction() {
        return action;
    }

    public Status getStatus() {
        return status;
    }

    public DataException getError() {
        return error;
    }

    public void setError(DataException e) {
        this.error = e;
        this.status = Status.ERROR;
    }

    public ObjectState getObjectState() {
        if (status == Status.FOUND) {
            return new ObjectState(getObjectClass(), getObjectId(), getObjectData());
        }

        return null;
    }

}
