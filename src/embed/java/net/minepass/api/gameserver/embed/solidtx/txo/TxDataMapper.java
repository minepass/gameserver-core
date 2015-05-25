/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.txo;

import net.minepass.api.gameserver.embed.solidtx.TxStack;
import net.minepass.api.gameserver.embed.solidtx.core.object.DataMapper;
import net.minepass.api.gameserver.embed.solidtx.core.object.DataMapperException;
import net.minepass.api.gameserver.embed.solidtx.core.object.ObjectState;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TxDataMapper implements DataMapper {

    @Override
    public boolean isClassSupported(Class objectClass) {
        return TxObject.class.isAssignableFrom(objectClass);
    }

    private void checkClass(Class objectClass) throws DataMapperException {
        if ( ! isClassSupported(objectClass)) {
            throw new DataMapperException("Incompatible class.");
        }
    }

    @Override
    public Object newObject(Class objectClass) throws DataMapperException {
        checkClass(objectClass);

        try {
            return objectClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new DataMapperException(e);
        }
    }

    @Override
    public Object loadObject(TxStack stack, ObjectState state) throws DataMapperException {
        Object o = newObject(state.getObjectClass());

        // Set object id.
        ((TxObject) o).setId(state.getObjectId());

        // Load data.
        reloadObject(stack, o, state);

        return o;
    }

    @Override
    public void reloadObject(TxStack stack, Object object, ObjectState newState) throws DataMapperException {
        checkClass(object.getClass());
        TxFieldException fieldException = null;

        if (object.getClass() != newState.getObjectClass()) {
            throw new DataMapperException(String.format(
                    "Reload object type mismatch. (%s <> %s)",
                    object.getClass().getSimpleName(),
                    newState.getObjectClass().getSimpleName()
            ));
        }

        if ( ! ((TxObject)object).getId().equals(newState.getObjectId())) {
            throw new DataMapperException(String.format(
                    "Reload object id mismatch. (%s <> %s)",
                    ((TxObject)object).getId(),
                    newState.getObjectId()
            ));
        }

        Map<String,Object> data = newState.getObjectData();

        //TODO only modify unchanged fields when state tracking implemented

        // Load via field annotations.
        for (Field f : object.getClass().getFields()) {
            Class ft = f.getType();
            try {
                TxField xf = f.getAnnotation(TxField.class);
                if (xf != null) {

                    boolean dereference = fieldIsReference(ft);

                    String key = xf.key();
                    if (key.isEmpty()) {
                        key = f.getName();
                        if (dereference) {
                            if (ft.isArray()) {
                                key += "_ids";
                            } else {
                                key += "_id";
                            }
                        }
                    }

//                    TxStack.debug(String.format(
//                            "Loading field (%s) from (%s) %s",
//                            f.getName(),
//                            key,
//                            (dereference ? "DEREF" : "")
//                            ));

                    if ( ! data.containsKey(key) || data.get(key) == null) {
                        if (xf.optional()) {
                            continue;
                        } else {
                            TxStack.debug("Missing data for required field.", this);
                            throw new TxFieldException(f.getName(), "Missing data for required field.");
                        }
                    }

                    try {
                        if (ft.isArray()) {
                            Object vc = data.get(key);
                            if ( ! Collection.class.isAssignableFrom(vc.getClass())) {
                                TxStack.debug("Array field requires collection as data source.", this);
                                throw new TxFieldException(f.getName(), "Array field requires collection as data source.");
                            }
                            Collection c = (Collection) vc;
                            if (dereference) {
                                Object[] v = new Object[c.size()];
                                int i = 0;
                                for (Object referencedId : c) {
                                    v[i++] = stack.getStoredObject(ft.getComponentType(), (Comparable) referencedId);
                                }
                                f.set(object, Arrays.copyOf(v, v.length, ft));
                            } else {
                                Object[] v = c.toArray();
                                f.set(object, Arrays.copyOf(v, v.length, ft));
                            }
                        } else {
                            Object v;
                            if (dereference) {
                                v = stack.getStoredObject(ft, (Comparable) data.get(key));
                            } else {
                                v = data.get(key);
                            }
                            if (ft.equals(Integer.class)) {
                                v = ((Long)v).intValue();
                            }
                            if (ft.equals(UUID.class)) {
                                v = UUID.fromString((String) v);
                            }
                            f.set(object, v);
                        }
                    } catch (IllegalAccessException e) {
                        TxStack.debug("Illegal field access.", this);
                        throw new TxFieldException(f.getName(), "Illegal field access.");
                    }

                }
            } catch (TxFieldException e) {
                if (fieldException != null) {
                    e.setPreviousException(fieldException);
                }
                fieldException = e;
            }
        }

        if (fieldException != null) {
            throw new DataMapperException("One or more fields failed to load.", fieldException);
        }
    }

    @Override
    public ObjectState dumpObjectState(Object object) throws DataMapperException {
        checkClass(object.getClass());

        return new ObjectState(
                object.getClass(),
                ((TxObject) object).getId(),
                dumpDataMap(object)
        );
    }

    protected Map<String, Object> dumpDataMap(Object object) throws DataMapperException {
        HashMap data = new HashMap<String,Object>();

        // Dump via field annotation.
        for (Field f : object.getClass().getFields()) {
            TxField xf = f.getAnnotation(TxField.class);
            if (xf != null) {
                String key = xf.key();
                if (key.isEmpty()) {
                    key = f.getName();
                }

                try {
                    Object value = f.get(object);
                    if (fieldIsReference(f.getType())) {
                        //TODO unimplemented (will be omitted in output)
                    } else {
                        data.put(key, value);
                    }
                } catch (IllegalAccessException e) {
                    TxStack.debug("Illegal field access.", this);
                    throw new DataMapperException("Illegal field access.", e);
                }
            }
        }

        return data;
    }

    protected boolean fieldIsReference(Class fieldType) {
        if (fieldType.isArray()) {
            return TxObject.class.isAssignableFrom(fieldType.getComponentType());
        } else {
            return TxObject.class.isAssignableFrom(fieldType);
        }
    }
}
