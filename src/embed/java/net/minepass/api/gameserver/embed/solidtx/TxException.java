/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx;

public class TxException extends Exception {

    public static final int UNKNOWN        = -1;
    public static final int UNAUTHORIZED   = 401;
    public static final int NOT_FOUND      = 404;

    protected int errorCode = UNKNOWN;

    public TxException() {
    }

    public TxException(String message) {
        super(message);
    }

    public TxException(String message, Throwable cause) {
        super(message, cause);
    }

    public TxException(Throwable cause) {
        super(cause);
    }

    public TxException(int errorCode) {
        this.errorCode = errorCode;
    }

    public TxException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TxException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TxException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean compareErrorCode(int compareWith) {
        return (getErrorCode() == compareWith);
    }

    public boolean isNotFound() {
        return compareErrorCode(NOT_FOUND);
    }
}
