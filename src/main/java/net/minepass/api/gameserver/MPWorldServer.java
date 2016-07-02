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

package net.minepass.api.gameserver;

import net.minepass.api.gameserver.embed.solidtx.txo.TxBaseObject;
import net.minepass.api.gameserver.embed.solidtx.txo.TxField;

import java.util.Map;
import java.util.UUID;

public class MPWorldServer extends TxBaseObject {

    @TxField
    public UUID world_id;

    @TxField
    public String join_url;

    @TxField
    public Boolean whitelist_imported;

    @TxField
    public Long clock_master;

    @TxField
    public Integer sync_interval;

    @TxField
    public Long reload_epoch;

    @TxField
    public MPPlayer founder;

    @TxField
    public MPPlayer[] players;

    /**
     * Maps in-game-name to uuid or other gameserver specific
     * bypass information. Guaranteed to not overlap with
     * players field.
     */
    @TxField
    public Map<String,String> bypass_players;

    /**
     * -1 if a maintenance is not scheduled.
     */
    @TxField
    public Long maintenance_epoch;

    public boolean isPlayerWhitelisted(UUID player_uuid) {
        for (MPPlayer p : players) {
            if (p.getId().equals(player_uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerBypassed(UUID player_uuid, String player_name) {
        for (String name : bypass_players.keySet()) {
            if (player_name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
