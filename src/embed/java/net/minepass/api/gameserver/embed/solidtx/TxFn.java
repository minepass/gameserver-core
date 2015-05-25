/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx;

/**
 * Function interface used to create entity callbacks.
 */
public interface TxFn {

    /**
     * Function called after entity is available,
     * or a final error is received/determined.
     *
     * @param stack the TxStack
     * @param obj the result entity or null
     * @param e exception if object is null
     */
    public void run(TxStack stack, Object obj, TxException e);

}
