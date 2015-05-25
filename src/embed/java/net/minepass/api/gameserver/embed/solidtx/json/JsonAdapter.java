/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.json;

import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.data.DataIndex;
import net.minepass.api.gameserver.embed.solidtx.core.data.DataSet;
import net.minepass.api.gameserver.embed.solidtx.core.network.DataRequest;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkAdapter;
import net.minepass.api.gameserver.embed.solidtx.core.network.Payload;
import net.minepass.api.gameserver.embed.solidtx.core.network.PayloadRequest;
import net.minepass.api.gameserver.embed.solidtx.core.storage.SD0A;
import net.minepass.api.gameserver.embed.solidtx.core.storage.DataStore;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageAdapter;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageIndex;
import net.minepass.api.gameserver.embed.solidtx.embed.json.JSONObject;
import net.minepass.api.gameserver.embed.solidtx.embed.json.parser.JSONParser;
import net.minepass.api.gameserver.embed.solidtx.embed.json.parser.ParseException;

import java.io.*;
import java.util.Map;

public class JsonAdapter implements NetworkAdapter, StorageAdapter, SD0A {

    JSONParser parser;

    public JsonAdapter() {
        this.parser = new JSONParser();
    }

    // Network
    // ------------------------------------------------------------------------------------------------------------- //

    @Override
    public byte[] encodePayloadRequest(PayloadRequest request) {
        JSONObject encoded = new JSONObject();

        StorageIndex storageIndex;
        if ( (storageIndex = request.getStorageIndex()) != null ) {
            encoded.put("meta", storageIndex.getAttribute("meta"));
            encoded.put("have", storageIndex.getMap());
        } else {
            encoded.put("attr", null);
            encoded.put("have", null);
        }

        DataIndex wantIndex = new DataIndex();
        DataSet sendSet = new DataSet();

        for (DataRequest dr : request.getDataRequests()) {
            if (dr.getAction() == DataRequest.Action.PULL) {
                wantIndex.add(
                        dr.getRoute().getDataGroup(),
                        dr.getObjectId()
                );
            } else if (dr.getAction() == DataRequest.Action.PUSH) {
                sendSet.add(
                        dr.getRoute().getDataGroup(),
                        dr.getObjectId(),
                        dr.getObjectData()
                );
            }
        }

        encoded.put("want", wantIndex.getMap());
        encoded.put("send", sendSet.getMap());

        TxStack.debug("Encoding payload request:", this);
        TxStack.debug(encoded.toString(), this);
        return encoded.toJSONString().getBytes();
    }

    @Override
    public Payload decodePayload(InputStream io) {
        InputStreamReader input = new InputStreamReader(io);

        TxStack.debug("Decoded payload:", this);

        JSONObject root;
        try {
            root = (JSONObject) parser.parse(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TxStack.debug(root.toString(), this);

        Map updates = ((JSONObject) root.get("data"));
        String meta = (String) root.get("meta");

        return new Payload(new DataSet(updates), meta);
    }

    // Storage
    // ------------------------------------------------------------------------------------------------------------- //

//    @Override
//    public void encodeDataStoreAttributes(DataStore input, OutputStream output) throws IOException {
//        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(output));
//        JSONObject attrs = new JSONObject(input.getAttributes());
//        attrs.writeJSONString(w);
//        w.close();
//    }
//
//    @Override
//    public void decodeDataStoreAttributes(DataStore output, InputStream input) throws IOException, ParseException {
//        BufferedReader r = new BufferedReader(new InputStreamReader(input));
//        JSONObject attrs = (JSONObject) parser.parse(r);
//        r.close();
//        output.setAttributes(attrs);
//    }

    @Override
    public void encodeDataStore(DataStore input, OutputStream output) throws IOException {
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(output));
        JSONObject root = new JSONObject();
        root.put("attr", input.getAttributes());
        root.put("data", new JSONObject(input.getMap()));
        root.writeJSONString(w);
        w.close();
    }

    @Override
    public void decodeDataStore(DataStore output, InputStream input) throws IOException, ParseException {
        BufferedReader r = new BufferedReader(new InputStreamReader(input));
        JSONObject root = (JSONObject) parser.parse(r);
        r.close();
        output.setAttributes((Map) root.get("attr"));
        output.setMap((Map<String, Map<String, Map<String, Object>>>) root.get("data"));
    }

}
