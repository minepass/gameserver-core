/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx;

import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkException;

public class TxSync implements Runnable {

    private TxStack stack;
    private Integer refreshSeconds;
    private final int minimumRefreshSeconds = 5;

    public TxSync(TxStack stack, Integer refreshSeconds) {
        this.stack = stack;
        setRefreshSeconds(refreshSeconds);
    }

    @Override
    public void run() {
        stack.log.info("Sync thread started.", this);
        while (true) {
            try {
                stack.log.debug("Next sync in " + getRefreshInterval() + " milliseconds.", this);
                Thread.sleep(getRefreshInterval());
            } catch (InterruptedException e) {
                stack.log.info("Sync thread stopped.", this);
                return;
            }
            try {
                stack.log.debug("Beginning sync.", this);
                stack.sync();
                stack.log.debug("Sync completed.", this);
                Integer newDynamicRefresh = getDynamicRefreshSeconds();
                if (newDynamicRefresh != null && newDynamicRefresh != getRefreshSeconds()) {
                    setRefreshSeconds(newDynamicRefresh);
                }
            } catch (NetworkException e) {
                e.printStackTrace();
                if (e.isTemporary()) {
                    stack.log.warn("Sync failed due to temporary network error.", this);
                } else {
                    stack.log.warn("Sync failed.", this);
                }
            }
        }
    }

    public Integer getRefreshSeconds() {
        return refreshSeconds;
    }

    public void setRefreshSeconds(Integer seconds) {
        this.refreshSeconds = seconds;
        stack.log.info("Refresh interval = " + seconds + " seconds.", this);
    }

    private Integer getRefreshInterval() {
        if (refreshSeconds != null && refreshSeconds >= minimumRefreshSeconds) {
            return refreshSeconds * 1000;
        }

        return minimumRefreshSeconds * 1000;
    }

    protected Integer getDynamicRefreshSeconds() {
        if (stack instanceof TxDynamicSync) {
            return ((TxDynamicSync) stack).getDynamicRefreshSeconds();
        }

        return null;
    }
}
