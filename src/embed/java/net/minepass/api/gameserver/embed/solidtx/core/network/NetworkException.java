/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.network;

import net.minepass.api.gameserver.embed.solidtx.TxException;

public class NetworkException extends TxException {

    protected Boolean temporaryException;

    public boolean isTemporary() {
        if (temporaryException == null) {
            // Attempt to determine if temporary.
            //
            return false;
        }

        return temporaryException;
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(int errorCode) {
        super(errorCode);
    }

    public NetworkException(int errorCode, String message) {
        super(errorCode, message);
    }

    public NetworkException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public NetworkException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
