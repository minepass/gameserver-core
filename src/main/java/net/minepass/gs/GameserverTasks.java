/*
 *  This file is part of MinePass, licensed under the MIT License (MIT).
 *
 *  Copyright (c) MinePass.net <http://www.minepass.net>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.minepass.gs;

import net.minepass.api.gameserver.MPPlayer;
import net.minepass.api.gameserver.MPWorldServer;
import net.minepass.api.gameserver.MPWorldServerMetric;
import net.minepass.api.gameserver.MinePass;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A core list of tasks necessary to keep a gameserver
 * in valid and working order. Primarily ensuring that
 * player passes remain valid throughout gameplay.
 */
public abstract class GameserverTasks {

    private MinePass minepass;
    private Long reloadLastEpoch;
    private Long metricLastEpoch;
    private Integer metricIntervalSeconds = 60;
    private Set<UUID> lastSeenPlayerIds;
    private int[] maintenceWarnings = new int[]{1, 5, 10, 15, 30, 60};
    private Integer lastMaintenanceWarning;

    public GameserverTasks(MinePass minepass) {
        this.minepass = minepass;
        this.reloadLastEpoch = minepass.getServer().reload_epoch;
        this.metricLastEpoch = null;
        this.lastSeenPlayerIds = Collections.emptySet();
    }

    public void runTasks() {
        // Reload local whitelist as needed.
        MPWorldServer server = minepass.getServer();
        if (!server.reload_epoch.equals(reloadLastEpoch)) {
            this.reloadLastEpoch = server.reload_epoch;
            updateAndReloadLocalAuth();
        }

        Map<UUID, String> currentPlayers = getCurrentPlayers();
        Long maintMinutes = minepass.getMinutesUntilServerMaintenance();

        MPWorldServerMetric metric = null;
        if (metricLastEpoch == null || System.currentTimeMillis()/1000 - metricLastEpoch > metricIntervalSeconds) {
            metric = new MPWorldServerMetric();
            metricLastEpoch = System.currentTimeMillis()/1000;
        }

        // Check current players (kick where invalid).
        for (UUID playerId : currentPlayers.keySet()) {
            String playerName = currentPlayers.get(playerId);
            MPPlayer player = minepass.getPlayer(playerId);
            boolean justLoggedIn = !(lastSeenPlayerIds.contains(playerId));

            // Kick if in maintenance.
            if (maintMinutes != null && maintMinutes < 1) {
                if (player == null || !player.has_admin_role) {
                    kickPlayerNext(justLoggedIn, playerId, "Server under maintenance.");
                }
            }

            /**
             * If player is not registered in MinePass or otherwise
             * unauthorized on this server, then kick unless bypassed
             * or option [enforce_whitelist]=false.
             */
            if (player == null) {
                if (minepass.getServer().isPlayerBypassed(playerId, playerName)) {
                    if (justLoggedIn) {
                        minepass.log.warn("Not kicking player ".concat(playerName).concat(" because they are on the import/bypass list."), this);
                        warnPlayerPass(playerId, "Welcome ".concat(playerName).concat(". This world is now managed through MinePass. Your membership has been imported."));
                    }
                } else if (minepass.getEnforceWhitelist()) {
                    kickPlayerNext(justLoggedIn, playerId,
                            "You do not have a MinePass for this server. " + getJoinUrl()
                    );
                } else {
                    if (justLoggedIn) {
                        minepass.log.info("Not kicking player ".concat(playerName).concat(" because [enforce_whitelist]=false"), this);
                        warnPlayerPass(playerId, minepass.getServer().world_greeting);
                    }
                }
            } else {
                // Ensure player is valid.
                //
                if (!player.isPassCurrent(minepass)) {
                    kickPlayerNext(justLoggedIn, playerId,
                            "Your MinePass to this world is expired. " + getJoinUrl()
                    );
                } else if (!player.name.equalsIgnoreCase(playerName)) {
                    kickPlayerNext(justLoggedIn, playerId,
                            "Your name has changed, please re-verify your player. " + getJoinUrl()
                    );
                } else if (metric != null) {
                    metric.addOnlinePlayer(player);
                }
            }
        }

        // Update last seen players, so that we can determine recent logins.
        lastSeenPlayerIds = currentPlayers.keySet();

        // Warn maintenance.
        if (maintMinutes == null || lastMaintenanceWarning == null) {
            this.lastMaintenanceWarning = 9999;
        } else if (maintMinutes < lastMaintenanceWarning) {
            for (int m : maintenceWarnings) {
                if (maintMinutes <= m && m < lastMaintenanceWarning) {
                    minepass.log.warn(String.format("Server maintenance in %d minutes.", maintMinutes), this);
                    warnAllPlayers(String.format("Server maintenance in %d minutes.", maintMinutes));
                    lastMaintenanceWarning = m;
                    break;
                }
            }
        }

        // Transmit server metric.
        if (metric != null) {
            minepass.sendObject(metric, null);
        }
    }

    protected String getJoinUrl() {
        return minepass.getServer().join_url;
    }

    protected void warnAllPlayers(String message) {
        for (UUID playerId : getCurrentPlayers().keySet()) {
            warnPlayer(playerId, message);
        }
    }

    /**
     * Kick player unless just logged in.
     *
     * Some clients will raise broken pipe errors if the server kicks
     * the player quickly after login. This helper is for code clarity
     * and effectively causes players to be kicked on the next run
     * of GameserverTasks.
     *
     * @param justLoggedIn player recent logged in
     * @param playerId player uuid
     * @param message kick message
     */
    protected void kickPlayerNext(Boolean justLoggedIn, UUID playerId, String message) {
        if (!justLoggedIn) {
            kickPlayer(playerId, message);
        }
    }

    /**
     * @return [uuid] = playerName
     */
    abstract protected Map<UUID, String> getCurrentPlayers();

    abstract protected void updateAndReloadLocalAuth();

    abstract protected void kickPlayer(UUID playerId, String message);

    abstract protected void warnPlayer(UUID playerId, String message);

    abstract protected void warnPlayerPass(UUID playerId, String message);


}
