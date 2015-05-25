/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.http;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class HttpController {

    public boolean forceSecure  = false;
    public boolean authRequiresSecure = true;

    public enum Method { GET, PUT, POST, DELETE }

    private HttpAuth auth;
    private SSLContext sslContext;
    private String userAgent = "SolidTX";

    public HttpController() {
    }


    // Configuration
    // ------------------------------------------------------------------------------------------------------------- //

    public void setAuth(HttpAuth auth) {
        this.auth = auth;
    }

    public void setCustomCertificateAuthority(InputStream certificateInput) throws GeneralSecurityException {
        // Read Certificate from InputStream.
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(certificateInput);

        // Create KeyStore.
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try {
            keyStore.load(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyStore.setCertificateEntry("ca", ca);

        // Create TrustManager.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create SSLContext.
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        this.sslContext = context;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    // Main Transport
    // ------------------------------------------------------------------------------------------------------------- //

//    public String http_method(Method m, String url) throws HttpException {
//        return http_method(m, url, null, 0);
//    }
//
//    public String http_method(Method m, String url, InputStream ioInput, int contentLength) throws HttpException {
//        try {
//            return http_method(m, new URL(url), ioInput, contentLength);
//        } catch (MalformedURLException e) {
//            throw new HttpException(e.getMessage(), e);
//        }
//    }

    public String http_method(Method m, URL url) throws HttpException {
        return http_method(m, url, null, 0, null);
    }

    public String http_method(Method m, URL url, InputStream ioInput, int contentLength, String contentType) throws HttpException {
        BufferedReader read;
        String line;
        String result = "";

        boolean useSSL = url.getProtocol().equals("https");
        if ( ! useSSL && forceSecure ) {
            throw new HttpException(HttpException.INSECURE, "This controller requires HTTPS URLs.");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", userAgent);

            if (sslContext != null) {
                ((HttpsURLConnection)conn).setSSLSocketFactory(sslContext.getSocketFactory());
            }

            if (auth != null) {
                if ( !useSSL && authRequiresSecure) {
                    throw new HttpException(HttpException.INSECURE, "Insecure auth not enabled for HTTP.");
                }
                auth.applyAuthToConnection(conn);
            }

            String requestMethod = "";
            switch (m) {
                case GET:
                    requestMethod = "GET";
                    break;
                case PUT:
                    requestMethod = "PUT";
                    break;
                case POST:
                    requestMethod = "POST";
                    break;
                case DELETE:
                    requestMethod = "DELETE";
                    break;
            }

            conn.setRequestMethod(requestMethod);

            if (contentLength > 0) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
                if (contentType != null) {
                    conn.setRequestProperty("Content-Type", contentType);
                }
                OutputStream ioOutput = conn.getOutputStream();
                byte[] inputBuffer = new byte[1024*10];  // 10 KB
                int inputLength;
                while ((inputLength = ioInput.read(inputBuffer)) != -1) {
                    ioOutput.write(inputBuffer, 0, inputLength);
                }
                ioOutput.flush();
                ioOutput.close();
                ioInput.close();
            }

            read = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = read.readLine()) != null) {
                result += line;
            }

            read.close();

        } catch (IOException e) {
            int responseCode = 0;
            try {
                if (conn != null) {
                    responseCode = conn.getResponseCode();
                }
            } catch(Exception nil) { }

            throw HttpException.fromIOException(e, responseCode);
        }

        return result;
    }


    // Transport Helpers
    // ------------------------------------------------------------------------------------------------------------- //

    public String http_get(String url) throws HttpException {
        try {
            return http_get(new URL(url));
        } catch (MalformedURLException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public String http_get(URL url) throws HttpException {
        return http_method(Method.GET, url);
    }

    public String http_put(String url) throws HttpException {
        try {
            return http_put(new URL(url));
        } catch (MalformedURLException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public String http_put(URL url) throws HttpException {
        return http_method(Method.PUT, url);
    }

    public String http_post(String url, String postData) throws HttpException {
        try {
            return http_post(new URL(url), postData);
        } catch (MalformedURLException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public String http_post(URL url, String postData) throws HttpException {
        if (postData == null) {
            return http_method(Method.POST, url, null, 0, null);
        }

        int length = postData.getBytes().length;
        InputStream ioInput = new ByteArrayInputStream(postData.getBytes());
        return http_method(Method.POST, url, ioInput, length, null);
    }

    public String http_delete(String url) throws HttpException {
        try {
            return http_delete(new URL(url));
        } catch (MalformedURLException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    public String http_delete(URL url) throws HttpException {
        return http_method(Method.DELETE, url);
    }

}
