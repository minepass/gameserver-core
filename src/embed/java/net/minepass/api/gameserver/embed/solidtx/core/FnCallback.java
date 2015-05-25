/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core;

import net.minepass.api.gameserver.embed.solidtx.TxException;
import net.minepass.api.gameserver.embed.solidtx.TxFn;
import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.network.DataRequest;

public class FnCallback {

    private DataRequest request;
    private TxFn fn;
    private boolean complete;

    public FnCallback(DataRequest request, TxFn fn) {
        this.request = request;
        this.fn = fn;
        this.complete = false;
    }

    public DataRequest getRequest() {
        return request;
    }

    public void run(TxStack stack, Object object, TxException e) {
        fn.run(stack, object, e);
        this.complete = true;
    }

    public boolean isComplete() {
        return complete;
    }
}
