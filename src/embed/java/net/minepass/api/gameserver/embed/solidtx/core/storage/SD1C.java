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

import java.util.Map;

public interface SD1C {

    public void replaceDataGroup(String group, Map<String,Map<String,Object>> input) throws TxException;

    public Map<String,Map<String,Object>> retrieveDataGroup(String group) throws TxException;

}
