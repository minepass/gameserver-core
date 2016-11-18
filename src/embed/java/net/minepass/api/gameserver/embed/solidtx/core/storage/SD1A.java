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
public interface SD1A extends SD0A {

    /**
     * Encode DataStore attributes. (Dimension = 0+)
     *
     * @param input
     * @param output
     * @throws Exception
     */
    public void encodeDataStoreAttributes(DataStore input, OutputStream output) throws Exception;
    public void decodeDataStoreAttributes(DataStore output, InputStream input) throws Exception;

}
