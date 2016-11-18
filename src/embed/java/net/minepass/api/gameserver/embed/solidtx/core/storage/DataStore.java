/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.storage;

import net.minepass.api.gameserver.embed.solidtx.TxException;
import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.data.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extension of DataSet to support additional storage
 * metadata and helper functions.
 */
public class DataStore extends DataSet {

    protected Map<String,String> attributes;
    protected boolean everythingLoaded;
    protected List<String> loadedGroups;
    protected List<String> dirtyGroups;
    protected Boolean dirtyAttributes;
    protected Container container;

    /**
     * Create new DataStore linked to a storage Container.
     *
     * @see Container
     * @param container the container to load/save to.
     */
    public DataStore(String stackVersion, Container container) {
        super();

        this.attributes = new HashMap<String, String>();
        this.container = container;
        this.everythingLoaded = false;
        this.loadedGroups = new ArrayList<String>();
        this.dirtyGroups = new ArrayList<String>();
        this.dirtyAttributes = false;

        loadAttributes();

        String oldStackVersion = getAttribute("version");
        if (oldStackVersion != null && ! oldStackVersion.equals(stackVersion)) {
            TxStack.debug("Emptying DataStore due to stack version change.", this);
            removeAll();
        }
        setAttribute("version", stackVersion);
    }

    @Override
    public void add(String group, Comparable id, Map<String, Object> data) {
        if ( ! (container instanceof SD2C)) {
            // Need to pre-load group if group/id is not
            // individually addressable.
            //
            loadMinimumDimension(group);
        }

        /*
        If the container is Dimension >= 2 then it supports
        writing independently to a single group/id, and there
        is no need to mark the group dirty and flush later.
         */
        if (container instanceof SD2C) {
            // Save now.
            //
            try {
                ((SD2C)container).replaceObjectData(group, id, data);
            } catch (TxException e) {
                e.printStackTrace();
            }
            super.add(group, id, data);
        } else {
            // Save later.
            //
            super.add(group, id, data);
            markGroupDirty(group);
        }
    }

    @Override
    public Map<String, Object> get(String group, Comparable id) {
        loadMinimumDimension(group, id);
        return super.get(group, id);
    }

    @Override
    public void remove(String group, Comparable id) {
        if ( ! (container instanceof SD2C)) {
            // Need to pre-load group if group/id is not
            // individually addressable.
            //
            loadMinimumDimension(group);
        }

        /*
        If the container is Dimension >= 2 then it supports
        writing independently to a single group/id, and there
        is no need to mark the group dirty and flush later.
         */
        if (container instanceof SD2C) {
            // Save now.
            //
            try {
                ((SD2C)container).removeObjectData(group, id);
            } catch (TxException e) {
                e.printStackTrace();
            }
            super.remove(group, id);
        } else {
            // Save later.
            //
            super.remove(group, id);
            markGroupDirty(group);
        }
    }

    @Override
    public void removeAll() {
        container.empty();
        super.removeAll();
    }

    @Override
    public Map<String, Map<String, Map<String, Object>>> getMap() {
        loadMinimumDimension(true);
        return super.getMap();
    }

    @Override
    public Map<String, Map<String, Object>> getGroupMap(String group) {
        loadMinimumDimension(group);
        return super.getGroupMap(group);
    }

    @Override
    public StorageIndex getIndex() {
        return new StorageIndex(this);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
        this.dirtyAttributes = true;
    }

    /**
     * Flush any changes back to storage container.
     *
     * This guarantees an update to the container, but
     * changes can also be written immediately as needed.
     */
    public void flush() {
        if (dirtyGroups.isEmpty() && !dirtyAttributes) {
            return;  // no need to flush
        }

        if (container instanceof SD1C) {
            // Need to save attributes and groups as whole units.
            //
            saveAttributes();
            for (String group : dirtyGroups) {
                try {
                    ((SD1C)container).replaceDataGroup(group, super.getGroupMap(group));
                    markGroupClean(group);
                } catch (TxException e) {
                    e.printStackTrace();
                }
            }
        } else if (container instanceof SD0C) {
            // Need to save store as a whole.
            //
            try {
                ((SD0C)container).replaceDataStore(this);
                dirtyGroups.clear();
            } catch (TxException e) {
                e.printStackTrace();
            }
        } else {
            throw new Error("Container incapable of saving DataStore.");
        }
    }
    //TODO attribute flush?

    protected void markGroupDirty(String group) {
        // Only needed if direct save not possible.
        if ( ! (container instanceof SD2C)) {
            if (!dirtyGroups.contains(group)) {
                dirtyGroups.add(group);
            }
        }
    }

    protected void markGroupClean(String group) {
        dirtyGroups.remove(group);
    }

    protected void loadAttributes() {
        container.retrieveDataStoreAttributes(this);
    }

    protected void saveAttributes() {
        container.replaceDataStoreAttributes(this);
    }

    /**
     * Load data so that entire DataStore is primed.
     * This is an expensive operation and should be used with caution.
     *
     * It is called as a last resort to other loadMinimumDimension()
     * functions, as well as for getMap().
     *
     * @param thisMustBeTrue safety parameter, must be true
     */
    protected void loadMinimumDimension(boolean thisMustBeTrue) {
        if (everythingLoaded) {
            return;
        }

        if (container instanceof SD0C && thisMustBeTrue) {
            // Load entire DataStore.
            //
            TxStack.debug("Loading all data. (CAUTION)", this);
            try {
                ((SD0C)container).retrieveDataStore(this);
            } catch (TxException e) {
                // There should be no reason the whole DataStore couldn't load.
                throw new RuntimeException(e);
            }
            everythingLoaded = true;
        } else {
            throw new RuntimeException("Could not load DataStore from Container at D-0.");
        }
    }

    protected void loadMinimumDimension(String group) {
        if (loadedGroups.contains(group)) {
            return;
        }

        if(container instanceof SD1C) {
            // Can load group without loading entire DataStore.
            //
            TxStack.debug("Loading data group: " + group, this);
            try {
                Map m = ((SD1C)container).retrieveDataGroup(group);
                dataMap.put(group, m);
                loadedGroups.add(group);
            } catch (TxException e) {
                //TODO silently catch not found.
                e.printStackTrace();
            }
        } else {
            // Need to load entire DataStore to get this group.
            //
            loadMinimumDimension(true);
        }
    }

    protected void loadMinimumDimension(String group, Comparable id) {
        if (super.contains(group, id)) {
            return;
        }

        if (container instanceof SD2C) {
            // Can load group/id data directly without loading whole group.
            //
            TxStack.debug("Loading object data: " + group + ":" + id, this);
            try {
                Map m = ((SD2C)container).retrieveObjectData(group, id);
                super.add(group, id, m);
            } catch (TxException e) {
                //TODO silently catch not found.
                e.printStackTrace();
            }
        } else {
            // Need to load at least the whole group.
            //
            loadMinimumDimension(group);
        }
    }

}
