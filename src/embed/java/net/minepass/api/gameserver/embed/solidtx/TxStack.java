/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx;

import net.minepass.api.gameserver.embed.solidtx.core.FnCallback;
import net.minepass.api.gameserver.embed.solidtx.core.data.DataException;
import net.minepass.api.gameserver.embed.solidtx.core.network.DataRequest;
import net.minepass.api.gameserver.embed.solidtx.core.network.Gateway;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkException;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkManager;
import net.minepass.api.gameserver.embed.solidtx.core.network.Payload;
import net.minepass.api.gameserver.embed.solidtx.core.object.DataMapper;
import net.minepass.api.gameserver.embed.solidtx.core.router.ObjectRoute;
import net.minepass.api.gameserver.embed.solidtx.core.router.Router;
import net.minepass.api.gameserver.embed.solidtx.core.storage.Container;
import net.minepass.api.gameserver.embed.solidtx.nulled.NullStorageContainer;
import net.minepass.api.gameserver.embed.solidtx.core.network.PayloadBus;
import net.minepass.api.gameserver.embed.solidtx.core.object.ObjectManager;
import net.minepass.api.gameserver.embed.solidtx.core.object.ObjectState;
import net.minepass.api.gameserver.embed.solidtx.core.router.MissingRouteException;
import net.minepass.api.gameserver.embed.solidtx.core.storage.StorageManager;
import net.minepass.api.gameserver.embed.solidtx.txo.TxDataMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Map;

/**
 * Primary class of the Solid TX platform.
 *
 * The stack manages the object manager, network gateway(s),
 * and storage container(s)
 *
 * It should be subclassed for each project and/or API
 * implementation.
 */
public abstract class TxStack {

    public final TxLog log = new TxLog(this);

    protected boolean setupComplete = false;
    protected Router router;
    protected ObjectManager objectManager;
    protected NetworkManager networkManager;
    protected StorageManager storageManager;
    protected ArrayDeque<FnCallback> callbacks;
    protected Object context;

    /**
     * Create and setup stack components.
     */
    protected void setup() {
        setupMappers(objectManager);
        setupNetwork(networkManager);
        setupStorage(storageManager);
        preflightChecks();
        this.setupComplete = true;
    }

    /**
     * Add DataMapper(s) to ObjectManager.
     *
     * The mapping chain is responsible for mapping
     * raw data to objects. Each mapper is executed
     * in the order it is added to the stack.
     *
     * @see ObjectManager
     * @see DataMapper
     * @param om the object manager passed in by #setup()
     */
    protected void setupMappers(ObjectManager om) {
        om.addDataMapper(new TxDataMapper());
        //TODO manager.addDataMapper(new PojoDataMapper());
    }

    /**
     * Add Gateway(s) and ObjectRoute(s) to NetworkManager.
     *
     * Each gateway is responsible for transmitting object
     * data for a set of objects linked via routes.
     *
     * @see NetworkManager
     * @see Gateway
     * @see ObjectRoute
     * @param nm the network manager passed by #setup()
     */
    abstract protected void setupNetwork(NetworkManager nm);

    /**
     * Add Container(s) to StorageManager.
     *
     * There must be one storage container for each network
     * gateway. The linkage is determined by the same
     * order the gateway(s) are added.
     *
     * A null container is available to bypass local storage.
     *
     * @see StorageManager
     * @see Container
     * @see NullStorageContainer
     * @param sm the storage manager passed by #setup()
     */
    abstract protected void setupStorage(StorageManager sm);

    /**
     * Version of specific stack implementation.
     *
     * Changes to a stack's version will cause existing DataStores
     * and Containers to be emptied in the likely event that model
     * schemas were changed.
     *
     *
     * @return
     */
    abstract public String getVersion();

    public TxStack() {
        this.callbacks = new ArrayDeque<FnCallback>();
        this.router = new Router();
        this.objectManager = new ObjectManager();
        this.networkManager = new NetworkManager(getVersion());
        this.storageManager = new StorageManager(getVersion());
    }

    protected TxStack(Router r, ObjectManager om, NetworkManager nm, StorageManager sm) {
        this.callbacks = new ArrayDeque<FnCallback>();
        this.router = r;
        this.objectManager = om;
        this.networkManager = nm;
        this.storageManager = sm;
    }

    protected void preflightChecks() {
        int gwSize = networkManager.getGateways().size();
        int smSize = storageManager.getDataStores().size();

        if (gwSize < 1) {
            throw new Error("Network manager requires at least one gateway.");
        }

        if (gwSize != smSize) {
            throw new Error("There must be one datastore for every gateway.");
        }
    }

    protected void setupRequired() {
        if ( ! setupComplete ) {
            throw new Error("TxStack setup incomplete.");
        }
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public String getStackName() {
        return this.getClass().getSimpleName();
    }

    // Routing and Context
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Create ObjectRoute linking an entity class to a gateway and data group.
     *
     * @param objectClass the entity class
     * @param gateway the gateway to link to
     * @param dataGroup the data group to link to
     */
    protected void addRoute(Class objectClass, Gateway gateway, String dataGroup) {
        try {
            int channel = networkManager.getGateways().indexOf(gateway);
            router.addRoute(objectClass, gateway, dataGroup, channel);
        } catch (TxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ObjectRoute for an entity class.
     *
     * @param objectClass the entity class
     * @return the entity's route
     */
    protected ObjectRoute getRoute(Class objectClass) throws MissingRouteException {
        return router.getRoute(objectClass);
    }

    /**
     * Shared context object (pojo) used in callbacks.
     *
     * @return the context pojo
     */
    public Object getContext() {
        return context;
    }

    /**
     * @see #getContext()
     * @param context the context pojo
     */
    public void setContext(Object context) {
        this.context = context;
    }

    // Object Management
    // ------------------------------------------------------------------------------------------------------------- //

    private int nextTemporaryObjectIndex = 1;

    protected Comparable getNextTemporaryObjectId() {
        return String.format("+%d", nextTemporaryObjectIndex++);
    }

    /**
     * Execute function against an entity.
     *
     * If the entity is cached or stored, the callback is run
     * immediately. Otherwise the entity is requested from the
     * network and the callback run later during #run().
     *
     * If the entity cannot be retrieved, for any reason other than
     * connectivity issues (Ex: Not Found, Unauthorized, etc.)
     * then the callback function receives a null object and
     * the exception is returned as a parameter.
     *
     * @see #run()
     * @see TxFn
     * @param objectClass the entity class
     * @param id the entity id
     * @param fn the callback function
     * @return callback if delayed, otherwise null
     */
    public FnCallback callObject(Class objectClass, Comparable id, TxFn fn) {
        setupRequired();
        Object o;
        ObjectRoute r;

        // Determine object route.
        try {
            r = getRoute(objectClass);
        } catch (MissingRouteException e) {
            e.printStackTrace();
            return null;
        }

        // Get object from storage (if it exists).
        o = getStoredObject(r, id);

        // Execute function call now, if we have the object.
        if (o != null) {
            fn.run(this, o, null);
            return null;
        }

        // Request the object's data.
        DataRequest request = new DataRequest(r, id);
        networkManager.getPayloadBus(request.getRoute()).addDataRequest(request);

        // Generate and return a callback.
        FnCallback cb = new FnCallback(request, fn);
        callbacks.add(cb);
        return cb;
    }

    public void sendObject(Object object, TxFn fn) {
        setupRequired();
        ObjectRoute r;
        ObjectState os;

        // Determine object route.
        try {
            r = getRoute(object.getClass());
        } catch (MissingRouteException e) {
            e.printStackTrace();
            return;
        }

        // Pull state from object.
        try {
            os = objectManager.dumpObject(this, object);
            if (os.getObjectId() == null) {
                os.setObjectId(getNextTemporaryObjectId());
            }
        } catch (DataException e) {
            e.printStackTrace();
            return;
        }

        // Push a new request with the object's data.
        DataRequest request = new DataRequest(r, os);
        networkManager.getPayloadBus(request.getRoute()).addDataRequest(request);
        //TODO fn is unhandled
    }

    public Object getStoredObject(Class objectClass, Comparable id) {
        setupRequired();
        ObjectRoute r;

        // Determine object route.
        try {
            r = getRoute(objectClass);
        } catch (MissingRouteException e) {
            e.printStackTrace();
            return null;
        }

        return getStoredObject(r, id);
    }

    public Object getStoredObject(ObjectRoute route, Comparable id) {
        setupRequired();
        Object o = null;

        Map storedData = storageManager.getDataStore(route).get(route.getDataGroup(), id);
        if (storedData != null) {
            try {
                o = objectManager.loadObject(this, new ObjectState(
                        route.getObjectClass(), id, storedData
                ));
            } catch (DataException e) {
                e.printStackTrace();
            }
        }

        return o;
    }

    // Lifecycle Handlers
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Lifecycle - Sync with Network
     *
     * Request and process the next payload from network gateway(s).
     * This will fetch new entity objects and update stale ones.
     *
     * @throws NetworkException on network error
     */
    public void sync() throws NetworkException {
        setupRequired();
        for (PayloadBus bus : networkManager.getPayloadBuses()) {
            Payload p;
            p = bus.getNextPayload(storageManager.getDataStore(bus.getChannel()).getIndex());
            storageManager.processPayload(bus.getChannel(), p);
        }
    }

    /**
     * Lifecycle - Run Callbacks
     *
     * Examine callbacks and execute if the corresponding
     * payload has been received. Otherwise the callback
     * is returned to the deque.
     */
    public void run() {
        setupRequired();
        FnCallback cb, cb0;

        cb = cb0 = callbacks.poll();
        if (cb0 == null) {
            return;
        }

        log.debug("Running callbacks:", this);

        do {  // for each callback, until back at start or empty

            // Get request for callback.
            DataRequest cbRequest = cb.getRequest();

            if (cbRequest.getStatus() == DataRequest.Status.PENDING) {
                // Request pending, add to back of deque.
                //
                callbacks.add(cb);
                log.debug(" + Callback shelved, request pending.", this);

            } else {
                // Request completed (success or failure).
                //
                try {
                    Object o = objectManager.loadObject(this, cbRequest.getObjectState());
                    cb.run(this, o, cbRequest.getError());
                } catch (DataException e) {
                    e.printStackTrace();
                }
                log.debug(" + Callback executed.", this);
            }

        } while ((cb = callbacks.poll()) != null && cb != cb0);

        log.debug("Callbacks complete.", this);
    }

    // IO Helpers
    // ------------------------------------------------------------------------------------------------------------- //

    /**
     * Close InputStream without extra try/catch.
     *
     * @param input the input to close
     */
    static public void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // Ignore.
        }
    }

    /**
     * Close OutputStream without extra try/catch.
     *
     * @param output the output to close
     */
    static public void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // Ignore.
        }
    }

    // Debug
    // ------------------------------------------------------------------------------------------------------------- //

    public static boolean debug = false;

    public static void debug(String message, Object component) {
        if (debug) {
            TxLog.debugComponent(message, component);
        }
    }

}
