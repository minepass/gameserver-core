/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.core.object;

import java.util.Map;

public class ObjectState {

    protected Class objectClass;
    protected Comparable objectId;
    protected Map<String,Object> objectData;

    public ObjectState(Class objectClass, Comparable objectId, Map<String, Object> objectData) {
        if (objectClass == null) {
            throw new RuntimeException("ObjectState class cannot be null.");
        }
        if (objectData == null) {
            throw new RuntimeException("ObjectState data cannot be null.");
        }

        this.objectClass = objectClass;
        this.objectId = objectId;
        this.objectData = objectData;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }

    public Comparable getObjectId() {
        return objectId;
    }

    public void setObjectId(Comparable objectId) {
        this.objectId = objectId;
    }

    public Map<String, Object> getObjectData() {
        return objectData;
    }

    public void setObjectData(Map<String, Object> objectData) {
        this.objectData = objectData;
    }
}
