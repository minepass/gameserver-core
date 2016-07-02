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

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MPWorldServerDetails extends TxBaseObject {

    @TxField
    public String plugin_type;

    @TxField
    public String plugin_version;

    @TxField
    public String game_realm;

    @TxField
    public String game_version;

    @TxField(optional = true)
    public String game_version_raw;

    @TxField
    public List<String[]> plugins;  // [ [ NAME, VERSION, MAIN_CLASS ], ... ]

    @TxField(optional = true)
    public String import_whitelist_base64;

    public MPWorldServerDetails() {
        this.plugins = new ArrayList<>();
    }

    public void addPlugin(String name, String version, String mainClass) {
        plugins.add(new String[]{name, version, mainClass});
    }

    public void importWhitelist(String whitelist) {
        importWhitelist(whitelist.getBytes());
    }

    public void importWhitelist(File whitelist) {
        if (!whitelist.exists()) {
            importWhitelist(new byte[]{});
            return;
        }

        try {
            importWhitelist(Files.readAllBytes(Paths.get(whitelist.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read whitelist to import.", e);
        }
    }

    public void importWhitelist(byte[] whitelist) {
        if (whitelist.length == 0) {
            import_whitelist_base64 = "=";
        } else {
            import_whitelist_base64 = DatatypeConverter.printBase64Binary(whitelist);
        }
    }

}
