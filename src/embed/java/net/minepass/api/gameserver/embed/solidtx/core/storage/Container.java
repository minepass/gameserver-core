/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.storage;

/**
 * Persistent storage container for DataStores.
 *
 * DataStores are encoded/decoded through the use
 * of a StorageAdapter, typically to raw byte data.
 *
 * Containers can provide various "dimensions" of
 * storage indicating the granularity with which
 * they can store attributes, data groups, and
 * entities.
 *
 * The associated adapter must support at minimum
 * the storage dimensions of the container.
 *
 * Container dimensions:
 *   0 = Whole DataStore Only (Ex. Single File)
 *   1 = Separate data groups and attributes.
 *   2 = Separate entities and attributes.
 *
 * @see net.minepass.api.gameserver.embed.solidtx.core.storage.DataStore
 * @see net.minepass.api.gameserver.embed.solidtx.core.storage.StorageAdapter
 */
public abstract class Container {

    protected StorageAdapter adapter;

    /**
     * Create new Container using a StorageAdapter.
     *
     * @param adapter the storage adapter
     */
    public Container(StorageAdapter adapter) {
        this.adapter = adapter;

        int d = getDimension();
        if (d < 0) {
            throw new Error("Storage container has an invalid dimension. Check interfaces.");
        }
        if ( ! checkAdapterDimension(d)) {
            throw new Error("Storage adapter does not support the dimension of the container.");
        }
    }

    protected abstract StorageAdapter getAdapter();

    private int getDimension() {
        if (this instanceof SD2C) {
            return 2;
        } else if(this instanceof SD1C) {
            return 1;
        } else if(this instanceof SD0C) {
            return 0;
        } else {
            return -1;
        }
    }

    private boolean checkAdapterDimension(int containerDimension) {
        switch (containerDimension) {
            case 0:
                if (adapter instanceof SD0A)
                    return true;
                break;
            case 1:
                if (adapter instanceof SD1A)
                    return true;
                break;
            case 2:
                if (adapter instanceof SD2A)
                    return true;
                break;
        }
        return false;
    }

    public abstract void empty();

    public abstract void replaceDataStoreAttributes(DataStore input);

    public abstract void retrieveDataStoreAttributes(DataStore output);

}
