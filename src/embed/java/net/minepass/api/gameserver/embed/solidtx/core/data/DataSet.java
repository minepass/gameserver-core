/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides storage of raw data separated by a data group and id.
 *
 * DataSets are used by both the network and storage modules to represent
 * data at various points in the lifecycle.
 *
 * Data groups are translated to entity classes by the router module, and
 * each DataSet is specific to a gateway or container, since group names
 * are not necessarily unique across all network endpoints.
 */
public class DataSet {

    protected Map<String,Map<String,Map<String,Object>>> dataMap;

    /**
     * Create new DataSet.
     */
    public DataSet() {
        this.dataMap = new HashMap<String,Map<String,Map<String,Object>>>();
    }

    /**
     * Create new DataSet from raw data map.
     * @see #setMap(java.util.Map)
     * @param dataMap the initial data map
     */
    public DataSet(Map<String, Map<String, Map<String, Object>>> dataMap) {
        this.dataMap = dataMap;
    }

    // Data Entries
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Add data for group/id.
     *
     * @param group the data group
     * @param id the id
     * @param data key/value data
     */
    public void add(String group, Comparable id, Map<String, Object> data) {
        getGroupMap(group).put(String.valueOf(id), data);
    }

    /**
     * Get data for group/id.
     *
     * Returns null if missing.
     *
     * @param group the data group
     * @param id the id
     * @return key/value data or null
     */
    public Map<String,Object> get(String group, Comparable id) {
        return getGroupMap(group).get(String.valueOf(id));
    }

    /**
     * Check if DataSet contains data for group/id.
     *
     * @param group the data group
     * @param id the id
     * @return true when data exists
     */
    public boolean contains(String group, Comparable id) {
        return getGroupMap(group).containsKey(String.valueOf(id));
    }

    /**
     * Remove data for group/id.
     *
     * @param group the data group
     * @param id the id
     */
    public void remove(String group, Comparable id) {
        getGroupMap(group).remove(String.valueOf(id));
    }

    /**
     * Remove all data. (Empty the DataSet)
     */
    public void removeAll() {
        this.dataMap = new HashMap<String,Map<String,Map<String,Object>>>();
    }

    // Maps, Groups, Index
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * @see #setMap(java.util.Map)
     * @return the raw data map for all groups
     */
    public Map<String, Map<String, Map<String, Object>>> getMap() {
        return dataMap;
    }

    /**
     * Set the raw data map for all groups, as follows:
     * [GROUP_NAME][ENTITY_ID][KEY] = VALUE
     *
     * @param data
     */
    public void setMap(Map<String, Map<String, Map<String, Object>>> data) {
        this.dataMap = data;
    }

    /**
     * @return the names of all added groups
     */
    public Set<String> getGroupNames() {
        return dataMap.keySet();
    }

    /**
     * @param group the data group
     * @return the sub-map for a particular data group
     */
    public Map<String,Map<String,Object>> getGroupMap(String group) {
        Map<String,Map<String,Object>> map;

        if ( ! dataMap.containsKey(group)) {
            map = new HashMap<String, Map<String,Object>>();
            dataMap.put(group, map);
        } else {
            map = dataMap.get(group);
        }

        return map;
    }

    /**
     * Generate a DataIndex for this DataSet
     * @see DataIndex
     * @return the index
     */
    public DataIndex getIndex() {
        return new DataIndex(this);
    }

    // Merging
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Merge this DataSet with another by adding missing data and replacing
     * existing data on group/id basis.
     *
     * @param input the data set to merge onto this one
     */
    public void mergeWith(DataSet input) {
        for (String group : input.getGroupNames()) {
            for (Map.Entry<String, Map<String, Object>> object : input.getGroupMap(group).entrySet()) {
                add(group, String.valueOf(object.getKey()), object.getValue());
            }
        }
    }

}
