/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.data;

import net.minepass.api.gameserver.embed.solidtx.TxException;

public class DataException extends TxException {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(int errorCode) {
        super(errorCode);
    }

    public DataException(int errorCode, String message) {
        super(errorCode, message);
    }

    public DataException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public DataException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
