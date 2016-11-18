/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.http;

import net.minepass.api.gameserver.embed.solidtx.core.network.Gateway;
import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkAdapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;

public class HttpGateway extends Gateway {

    protected URL endpoint;
    protected HttpController controller;

    public HttpGateway(URL endpoint, NetworkAdapter adapter) {
        super(adapter);
        this.endpoint = endpoint;
        this.controller = new HttpController();
    }

    public HttpController getController() {
        return controller;
    }

    public void setEndpointAuth(HttpAuth auth) {
        controller.setAuth(auth);
    }

    public void setEndpointCertificateAuthority(InputStream certificateInput) throws GeneralSecurityException {
        controller.setCustomCertificateAuthority(certificateInput);
    }

    @Override
    protected InputStream txEncodedPayloadRequest(byte[] input) throws HttpException {
        return new ByteArrayInputStream(
                controller.http_method(
                        HttpController.Method.POST,
                        endpoint,
                        new ByteArrayInputStream(input),
                        input.length,
                        "application/octet-stream"
                ).getBytes()
        );
    }
}
