/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.txo;

import net.minepass.api.gameserver.embed.solidtx.core.object.DataMapperException;

public class TxFieldException extends DataMapperException {

    private String fieldName;
    private TxFieldException previousException;

    public TxFieldException(String fieldName, String message) {
        super(message + " (" + fieldName + ")");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TxFieldException getPreviousException() {
        return previousException;
    }

    public void setPreviousException(TxFieldException previousException) {
        this.previousException = previousException;
    }
}
