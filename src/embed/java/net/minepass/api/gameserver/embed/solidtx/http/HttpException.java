/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.http;

import net.minepass.api.gameserver.embed.solidtx.core.network.NetworkException;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.ConnectException;

public class HttpException extends NetworkException {

    static final int INSECURE = 900;
    static final int INVALID_SSL = 901;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(int errorCode) {
        super(errorCode);
    }

    public HttpException(int errorCode, String message) {
        super(errorCode, message);
    }

    public HttpException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public HttpException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public static HttpException fromIOException(IOException ioException, int responseCode) {
        HttpException eOut;
        eOut = new HttpException(ioException.getMessage(), ioException);

        if (responseCode >= 100 && responseCode < 600) {
            eOut.errorCode = responseCode;
        }

        try {
            throw ioException;

        } catch (ConnectException e) {
            eOut.temporaryException = true;

        } catch (SSLHandshakeException e) {
            eOut.errorCode = INVALID_SSL;
            eOut.temporaryException = true;

        } catch (IOException e) {
            // Nothing to do.
        }

        return eOut;
    }
}
