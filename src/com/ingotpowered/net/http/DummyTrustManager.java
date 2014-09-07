package com.ingotpowered.net.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class DummyTrustManager implements X509TrustManager {

    public static final DummyTrustManager instance = new DummyTrustManager();

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
