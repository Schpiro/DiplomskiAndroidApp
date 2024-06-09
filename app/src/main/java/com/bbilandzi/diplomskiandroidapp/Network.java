package com.bbilandzi.diplomskiandroidapp;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static final String BASE_URL = "https://192.168.1.88:8081/";
    private static final String PASSWORD = "]Bw[=.^s=&`zd=Id>C~C@brT";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = createOkHttpClient(context);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Use the custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static OkHttpClient createOkHttpClient(Context context) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        SSLSocketFactory sslSocketFactory = getTrustAllHostsSSLSocketFactory();
        if (sslSocketFactory != null) {
            client.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        }

        try {
            SSLContext sslContext = getSslContextForCertificateFile(context, "certificate2.crt");
            client.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client.build();
    }

    private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }
    };

    private static TrustManagerFactory trustManagerFactory;

    static {
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static SSLContext getSslContextForCertificateFile(Context context, String fileName) {
        try {
            System.setProperty("javax.net.debug", "all");
            KeyStore keyStore = getKeyStore(context, fileName);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            String msg = "Error during creating SslContext for certificate from assets";
            e.printStackTrace();
            throw new RuntimeException(msg);
        }
    }

    private static KeyStore getKeyStore(Context context, String fileName) {
        KeyStore keyStore = null;
        try {
            InputStream caInput = context.getResources().openRawResource(R.raw.certificate2);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d("SslUtilsAndroid", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    public static SSLSocketFactory getTrustAllHostsSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }

                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
