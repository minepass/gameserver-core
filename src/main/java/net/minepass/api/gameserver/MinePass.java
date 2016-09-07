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

import net.minepass.api.gameserver.embed.solidtx.TxDynamicSync;
import net.minepass.api.gameserver.embed.solidtx.TxException;
import net.minepass.api.gameserver.embed.solidtx.TxFn;
import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkException;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkManager;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageManager;
import net.minepass.api.gameserver.embed.solidtx.disk.FileStorageContainer;
import net.minepass.api.gameserver.embed.solidtx.http.HttpBasicAuth;
import net.minepass.api.gameserver.embed.solidtx.http.HttpGateway;
import net.minepass.api.gameserver.embed.solidtx.json.JsonAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class MinePass extends TxStack implements TxDynamicSync {

    protected static final JsonAdapter jsonAdapter = new JsonAdapter();

    protected final int initSyncRetries = 3;
    protected URL endpoint;
    protected String serverUUID;
    protected String serverSecret;
    protected String variant;
    protected HashMap<String,String> variantConfig;

    @Override
    public String getVersion() {
        return String.format("%s-%s", properties.getProperty("coreVersion"), properties.getProperty("coreRevision"));
    }

    @Override
    public String getStackName() {
        return "MinePass";
    }

    public String getVariant() {
        return variant;
    }

    public MinePass(MPConfig c) throws MPConfigException, MPStartupException {
        super();

        this.variant = c.variant;
        this.variantConfig = c.variant_config;

        if (c.api_host == null || c.api_host.isEmpty()) {
            throw new MPConfigException("MinePass API host must be provided.");
        }

        if (c.server_uuid == null || c.server_uuid.isEmpty()) {
            throw new MPConfigException("MinePass server UUID must be provided.");
        } else {
            this.serverUUID = c.server_uuid;
        }

        if (c.server_secret == null || c.server_secret.isEmpty()) {
            throw new MPConfigException("MinePass server secret-key must be provided.");
        } else {
            this.serverSecret = c.server_secret;
        }

        try {
            String api_host = c.api_host;
            if ( ! api_host.matches(".*\\.minepass\\.net(:[0-9]+)?") ) {
                throw new MPConfigException("Invalid MinePass API host. (" + api_host + ")");
            }
            if ( ! api_host.matches("[a-z]+://.*") ) {
                api_host = "https://" + api_host;
            }
            this.endpoint = new URL(api_host + "/gameserver-api/v0/payload");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new MPConfigException("Could not parse API host URL.", e);
        }

        setup();
        if (!initialSync()) {
            throw new MPStartupException("Failed to complete initial sync.");
        }
    }

    // General Information Queries
    // ------------------------------------------------------------------------------------------------------------- //

    public URL getEndpoint() {
        return endpoint;
    }

    public String getServerUUID() {
        return serverUUID;
    }

    public MPWorldServer getServer() {
        return (MPWorldServer) getStoredObject(MPWorldServer.class, getServerUUID());
    }

    public MPPlayer getPlayer(Comparable playerUUID) {
        return (MPPlayer) getStoredObject(MPPlayer.class, playerUUID);
    }

    public Long getMinutesUntilServerMaintenance() {
        if (getServer().maintenance_epoch < 0)
            return null;
        return Math.max(0L, getServer().maintenance_epoch  - getCalibratedEpoch()) / 60L;
    }

    // Sync and Stack Setup
    // ------------------------------------------------------------------------------------------------------------- //

    protected boolean initialSync() {
        MPWorldServer server = getServer();
        if (server == null) {
            log.info("Performing initial sync...", this);

            callObject(MPWorldServer.class, getServerUUID(), new TxFn() {
                @Override
                public void run(TxStack stack, Object o, TxException e) {
                    if (e != null) {
                        log.warn(e.getMessage(), this);
                    }
                }
            });

            for (int i = 0; i < initSyncRetries; i++) {
                if (i > 0) {
                    log.warn("Sync failed, retrying in 30 seconds.", this);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e2) {
                        break;
                    }
                }
                try {
                    sync();
                    break;
                } catch (NetworkException | RuntimeException e) {
                    e.printStackTrace();
                }
            }

            run();

            server = getServer();
            if (server == null) {
                log.warn("Failed to complete initial sync.", this);
                return false;
            } else {
                log.info("Initial sync completed successfully.", this);
            }

            // Recalculate clock drift.
            long localEpoch = System.currentTimeMillis() / 1000;
            long clockDrift = localEpoch - server.clock_master;
            log.info(String.format(
                    "Recalibrating clock: remote = %d, local = %d",
                    server.clock_master,
                    localEpoch
            ), this);
            setClockDrift(clockDrift);
        }

        log.info(String.format("Local server clock drift: %d seconds", getClockDrift()), this);
        return true;
    }

    @Override
    protected void setupNetwork(NetworkManager nm) {
        HttpGateway gw = new HttpGateway(endpoint, jsonAdapter);
        gw.getController().setUserAgent("SolidTX - MinePass/".concat(getVariant()));

        try {
            // Disable secure auth requirement if endpoint is localhost.
            if (endpoint.equals(new URL("http", "localhost", endpoint.getPort(), endpoint.getFile()))) {
                gw.getController().authRequiresSecure = false;
            }
        } catch (MalformedURLException e) {
            // Ignore.
        }

        gw.setEndpointAuth(new HttpBasicAuth(serverUUID, serverSecret));
        nm.addGateway(gw);

        addRoute(MPWorldServer.class, gw, "world-server");
        addRoute(MPWorldServerDetails.class, gw, "world-server-details");
        addRoute(MPWorldServerMetric.class, gw, "world-server-metric");
        addRoute(MPPlayer.class, gw, "player");
    }

    @Override
    protected void setupStorage(StorageManager sm) {
        sm.addContainer(new FileStorageContainer(
                jsonAdapter,
                "minepass_cache.json"
        ));
    }

    // Clock Utilities
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * The MinePass central servers adjust how often
     * the clients should poll for updates.
     *
     * @return interval or null if server unavailable
     */
    @Override
    public Integer getDynamicRefreshSeconds() {
        MPWorldServer server = getServer();
        if (server != null) {
            return server.sync_interval;
        }

        return null;
    }

    /**
     * Returns an epoch calibrated against MinePass servers.
     *
     * Due to the nature of Visitors (expiring) passes, maintaining an accurate clock
     * is critical to ensure that players are not kicked inaccurately.
     *
     * Most authentication systems place a hefty value on an accurate local clock;
     * however MinePass must recognize that shared hosting environments create
     * situations where server owners are not directly responsible for their clock's
     * accuracy. Waiting for support tickets to fix such issues would be off-putting.
     *
     * Although MinePass is mostly asynchronous, it does perform an initial sync at
     * install, upon software update, or after the removal of its cache file.
     *
     * As such we use that opportunity to remove any *significant* drift via the
     * MPWorldServer.clock_master field, the resulting calculation of which is
     * stored as an attribute within the SolidTX cache file.
     *
     * @return epoch calibrated against MinePass servers
     */
    public long getCalibratedEpoch() {
        return (System.currentTimeMillis() / 1000) - getClockDrift();
    }

    /**
     * @see #getCalibratedEpoch()
     * @return seconds local clock is ahead (or negative behind) the remote clock
     */
    public Long getClockDrift() {
        String drift = getStorageManager().getDataStore(0).getAttribute("clock_drift");
        if (drift == null) {
            return Long.valueOf(0);
        } else {
            return Long.valueOf(drift);
        }
    }

    /**
     * @see #getCalibratedEpoch()
     * @param drift seconds local clock is ahead (or negative behind) the remote clock
     */
    public void setClockDrift(Long drift) {
        getStorageManager().getDataStore(0).setAttribute("clock_drift", drift.toString());
    }

    // Properties FIle
    // ------------------------------------------------------------------------------------------------------------- //

    protected static final Properties properties;

    static {
        InputStream inputStream = MinePass.class.getResourceAsStream("/minepass.properties");
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }
        }
    }
}
