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

package net.minepass.gs.mc;

import net.minepass.api.gameserver.MPConfig;
import net.minepass.api.gameserver.MPConfigException;
import net.minepass.api.gameserver.MPPlayer;
import net.minepass.api.gameserver.MPStartupException;
import net.minepass.api.gameserver.MPWorldServer;
import net.minepass.api.gameserver.MinePass;
import net.minepass.api.gameserver.embed.solidtx.embed.json.JSONArray;
import net.minepass.api.gameserver.embed.solidtx.embed.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Minecraft specific extensions to the base MinePass SolidTX api stack.
 */
public class MinePassMC extends MinePass {

    static public final File whitelistFile = new File("whitelist.json");
    static public final File whitelistBackupFile = new File("whitelist.json.mpimport");

    public MinePassMC(MPConfig config) throws MPConfigException, MPStartupException {
        super(config);

        if (!whitelistFile.isFile()) {
            throw new MPConfigException("Could not locate whitelist.json");
        }
        if (!whitelistFile.canWrite()) {
            throw new MPConfigException("File whitelist.json is not writable.");
        }

        // Backup original whitelist.
        if (!getServer().whitelist_imported) {
            if (whitelistFile.length() > 0 && !whitelistBackupFile.exists()) {
                try {
                    Files.copy(Paths.get(whitelistFile.getPath()), Paths.get(whitelistBackupFile.getPath()));
                } catch (IOException e) {
                    throw new MPStartupException("Failed to backup whitelist for import.", e);
                }
            }
        }

        // Update whitelist.
        updateLocalWhitelist();
    }

    public void updateLocalWhitelist() {
        try {
            FileWriter fw = new FileWriter(whitelistFile, false);
            fw.write(getServerWhitelistJSON(getServer()).toJSONString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected JSONArray getServerWhitelistJSON(MPWorldServer server) {
        JSONArray ja = new JSONArray();
        for (MPPlayer p : server.players) {
            ja.add(getPlayerWhitelistJSON(p));
        }
        for (String uuid : server.bypass_players.keySet()) {
            ja.add(getPlayerWhitelistJSON(server.bypass_players.get(uuid), uuid));
        }
        return ja;
    }

    protected JSONObject getPlayerWhitelistJSON(MPPlayer player) {
        return getPlayerWhitelistJSON((String) player.getId(), player.name);
    }

    protected JSONObject getPlayerWhitelistJSON(String uuid, String name) {
        JSONObject jo = new JSONObject();
        jo.put("uuid", uuid);
        jo.put("name", name);
        return jo;
    }

}
