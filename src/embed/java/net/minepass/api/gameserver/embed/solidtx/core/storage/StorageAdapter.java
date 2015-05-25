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
 * Responsible for encoding/decoding DataStores to a
 * Container's external input/output streams.
 *
 * Different containers support different storage
 * dimensions, I.E. the granularity of what parts
 * of a DataStore can be read/write independently.
 *
 * This base interface is inherited by separate
 * interfaces for each progressively higher
 * storage dimension.
 *
 * See Container for more information.
 *
 * @see net.minepass.api.gameserver.embed.solidtx.core.storage.Container
 * @see net.minepass.api.gameserver.embed.solidtx.core.storage.DataStore
 */
public interface StorageAdapter {

}
