/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.nulled;

import net.minepass.api.gameserver.embed.solidtx.TxException;
import net.minepass.api.gameserver.embed.solidtx.core.storage.Container;
import net.minepass.api.gameserver.embed.solidtx.core.storage.DataStore;
import net.minepass.api.gameserver.embed.solidtx.core.storage.SD0C;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageAdapter;

public class NullStorageContainer extends Container implements SD0C {

    public NullStorageContainer() {
        super(new NullStorageAdapter());
    }

    @Override
    protected StorageAdapter getAdapter() {
        return null;
    }

    @Override
    public void empty() {
        // Nothing to do.
    }

    @Override
    public void replaceDataStoreAttributes(DataStore input) {
        // Nothing to do.
    }

    @Override
    public void retrieveDataStoreAttributes(DataStore output) {
        // Nothing to do.
    }

    public void replaceDataStore(DataStore input) throws TxException {
        // Nothing to do.
    }

    public void retrieveDataStore(DataStore output) throws TxException {
        // Nothing to do.
    }
}
