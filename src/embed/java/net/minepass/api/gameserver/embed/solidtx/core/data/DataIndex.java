/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a lightweight index of what a DataSet contains.
 *
 * Specifically reducing a DataSet to group/id combinations
 * without the associated key/value data.
 */
public class DataIndex {

    protected Map<String,List<String>> indexMap;

    /**
     * Create new DataIndex.
     */
    public DataIndex() {
        this.indexMap = new HashMap<String, List<String>>();
    }

    /**
     * Create new DataIndex from existing DataSet.
     * @param dataSet the data set to import from
     */
    public DataIndex(DataSet dataSet) {
        this();
        importFromDataSet(dataSet);
    }

    /**
     * Add group/id to index.
     *
     * @param group the group to add
     * @param id the id to add
     */
    public void add(String group, Comparable id) {
        getGroupList(group).add(String.valueOf(id));
    }

    /**
     * Get a Map of the index in the following format:
     * [GROUP_NAME] = [ID0,ID1,ID2,...]
     * @return
     */
    public Map<String,List<String>> getMap() {
        return indexMap;
    }

    protected List<String> getGroupList(String group) {
        List<String> groupEntries;

        if ( ! indexMap.containsKey(group)) {
            groupEntries = new ArrayList<String>();
            indexMap.put(group, groupEntries);
        } else {
            groupEntries = indexMap.get(group);
        }

        return groupEntries;
    }

    protected void importFromDataSet(DataSet dataSet) {
        for (Map.Entry<String,Map<String,Map<String,Object>>> entry : dataSet.getMap().entrySet()) {
            String dataGroup = entry.getKey();
            Set<String> keySet = entry.getValue().keySet();
            String[] groupEntries = keySet.toArray(new String[keySet.size()]);
            indexMap.put(dataGroup, Arrays.asList(groupEntries));
        }
    }

}
