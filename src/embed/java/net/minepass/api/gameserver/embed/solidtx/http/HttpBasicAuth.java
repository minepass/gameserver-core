/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.http;

import javax.xml.bind.DatatypeConverter;
import java.net.URLConnection;

public class HttpBasicAuth implements HttpAuth {

    private String user;
    private String pass;

    public HttpBasicAuth(String username, String passphrase) {
        this.user = username;
        this.pass = passphrase;
    }

    @Override
    public void applyAuthToConnection(URLConnection conn) {
        String userPass = user + ":" + pass;
        String encoding = DatatypeConverter.printBase64Binary(userPass.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }

}
