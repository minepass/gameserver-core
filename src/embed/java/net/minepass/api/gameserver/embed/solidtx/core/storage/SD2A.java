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
import java.util.Map;

/**
 * @see net.minepass.api.gameserver.embed.solidtx.core.storage.StorageAdapter
 */
public interface SD2A extends SD1A {

    public void encodeObjectData(Map<String,Object> input, OutputStream output) throws Exception;
    public Map<String,Object> decodeObjectData(InputStream input) throws Exception;

}
