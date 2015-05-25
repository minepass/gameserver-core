/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.nulled;

import net.minepass.api.gameserver.embed.solidtx.core.storage.DataStore;
import net.minepass.api.gameserver.embed.solidtx.core.storage.SD0A;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageAdapter;

import java.io.InputStream;
import java.io.OutputStream;

public class NullStorageAdapter implements StorageAdapter, SD0A {
    @Override
    public void encodeDataStore(DataStore input, OutputStream output) throws Exception {
        // Nothing to do.
    }

    @Override
    public void decodeDataStore(DataStore output, InputStream input) throws Exception {
        // Nothing to do.
    }
}
