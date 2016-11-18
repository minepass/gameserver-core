/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.storage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @see StorageAdapter
 */
public interface SD0A extends StorageAdapter {

    /**
     * Encode whole DataStore. (Dimension = 0)
     *
     * @param input the data store to encode
     * @param output the stream to write output do
     * @throws Exception -
     */
    public void encodeDataStore(DataStore input, OutputStream output) throws Exception;

    /**
     * Decode whole DataStore. (Dimension = 0)
     *
     * @param output the data store to write decoded data to
     * @param input the stream to read encoded data from
     * @throws Exception -
     */
    public void decodeDataStore(DataStore output, InputStream input) throws Exception;

}
