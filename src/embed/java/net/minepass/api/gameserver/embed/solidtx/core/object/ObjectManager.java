/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.object;

import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.data.DataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for initializing entities from raw data via the mapper chain.
 *
 * @see net.minepass.api.gameserver.embed.solidtx.core.object.DataMapper
 */
public class ObjectManager {

    protected List<DataMapper> mappers;

    /**
     * Create new ObjectManager.
     */
    public ObjectManager() {
        this.mappers = new ArrayList<DataMapper>(2);
    }

    /**
     * Add mapper responsible for mapping raw data to
     * entity fields. Each mapper is tried in the order
     * that they are added to the manager.
     *
     * @param mapper -
     */
    public void addDataMapper(DataMapper mapper) {
        mappers.add(mapper);
    }

    protected DataMapper getDataMapper(Class objectClass) throws DataException {
        DataMapper result = null;

        for (DataMapper m : mappers) {
            if (m.isClassSupported(objectClass)) {
                result = m;
                break;
            }
        }

        if (result == null) {
            throw new DataException("No mapper available for object class: " + objectClass.getSimpleName());
        }

        return result;
    }

    /**
     * Load an entity into the cache from its data.
     *
     * If the entity already exists in the cache, its data
     * will be updated and the existing reference returned.
     *
     * @param state the entity state
     * @return the entity (or null if state null)
     * @throws DataException on data mapping failure
     */
    public Object loadObject(TxStack stack, ObjectState state) throws DataException {
        if (state == null) {
            return null;
        }

        DataMapper m = getDataMapper(state.getObjectClass());
        return m.loadObject(stack, state);
    }


    /**
     * Reload an existing entity.
     *
     * @see #loadObject(net.minepass.api.gameserver.embed.solidtx.TxStack, ObjectState)
     *
     * @param object the previously loaded entity
     * @param newState updated entity state
     * @throws DataException on data mapping failure
     */
    public void reloadObject(TxStack stack, Object object, ObjectState newState) throws DataException {
        Class objectClass = object.getClass();
        DataMapper m = getDataMapper(objectClass);
        m.reloadObject(stack, object, newState);
    }

    public ObjectState dumpObject(TxStack stack, Object object) throws DataException {
        if (object == null) {
            return null;
        }

        DataMapper m = getDataMapper(object.getClass());
        return m.dumpObjectState(object);
    }
}
