/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.object;

import net.minepass.api.gameserver.embed.solidtx.core.data.DataException;

public class DataMapperException extends DataException {

    public DataMapperException(Throwable cause) {
        super(cause);
    }

    public DataMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataMapperException(String message) {
        super(message);
    }
}
