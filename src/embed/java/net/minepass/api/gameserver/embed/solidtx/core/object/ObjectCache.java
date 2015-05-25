/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.object;

import java.util.HashMap;
import java.util.Map;

/**
 * Used by ObjectManager to cache initialized entities for reuse.
 * Entities are stored according to their class, and separated by
 * a provided entity id.
 */
public class ObjectCache {

    protected Map<Class,Map<String,Object>> cache;

    /**
     * Should only be created by ObjectManager.
     * @see net.minepass.api.gameserver.embed.solidtx.core.object.ObjectManager
     */
    protected ObjectCache() {
        cache = new HashMap<Class,Map<String,Object>>();
    }

    /**
     * Add an entity to the cache.
     *
     * @param object the entity
     * @param id the entity id
     */
    public void addObject(Object object, Comparable id) {
        Class klass = object.getClass();
        cacheClassMap(klass).put(String.valueOf(id), object);
    }

    /**
     * Get an entity from the cache, based on class and id.
     *
     * Returns null on cache miss.
     *
     * @param objectClass the entity class
     * @param id the entity id
     * @return the entity or null
     */
    public Object getObject(Class objectClass, Comparable id) {
        return cacheClassMap(objectClass).get(String.valueOf(id));
    }

    /**
     * Remove an entity from the cache.
     *
     * @param object the entity
     * @param id the entity id
     */
    public void removeObject(Object object, Comparable id) {
        Class klass = object.getClass();
        removeObject(klass, id);
    }

    /**
     * Remove an entity from the class, based on class and id.
     *
     * @param objectClass the entity class
     * @param id the entity id
     */
    public void removeObject(Class objectClass, Comparable id) {
        cacheClassMap(objectClass).remove(String.valueOf(id));
    }

    /**
     * Check if cache contains matching entity.
     *
     * @param object the entity
     * @param id the entity id
     * @return true when entity in cache
     */
    public boolean containsObject(Object object, Comparable id) {
        if (object != null && getObject(object.getClass(), id) == object) {
            return true;
        } else {
            return false;
        }
    }

    protected Map<String,Object> cacheClassMap(Class objectClass) { //TODO consistent map naming
        Map<String,Object> map;

        if ( ! cache.containsKey(objectClass)) {
            map = new HashMap<String, Object>();
            cache.put(objectClass, map);
        } else {
            map = cache.get(objectClass);
        }

        return map;
    }
}
