package com.bbilandzi.diplomskiandroidapp.utils;

import android.content.Context;
import android.util.Log;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.repository.AuthRepository;
import com.bbilandzi.diplomskiandroidapp.repository.ContactsRepository;
import com.bbilandzi.diplomskiandroidapp.repository.EventRepository;
import com.bbilandzi.diplomskiandroidapp.repository.MessageRepository;
import com.bbilandzi.diplomskiandroidapp.BuildConfig;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class Network {
    private static final String BASE_URL = BuildConfig.BASE_URL;

    private static Retrofit retrofit = null;

    @Provides
    @Singleton
    public Retrofit getClient(@ApplicationContext Context context) {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .addInterceptor(new AuthInterceptor(AuthUtils.getToken(context)))
                    .build();

            //OkHttpClient client = createOkHttpClient(context);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @Provides
    @Singleton
    public AuthRepository getUserRepository(Retrofit client) {
        return new AuthRepository(client);
    }

    @Provides
    @Singleton
    public ContactsRepository getContactsRepository(Retrofit client) {
        return new ContactsRepository(client);
    }

    @Provides
    @Singleton
    public MessageRepository getMessagesRepository(Retrofit client) {
        return new MessageRepository(client);
    }

    @Provides
    @Singleton
    public EventRepository getEventRepository(Retrofit client) {
        return new EventRepository(client);
    }

    private static OkHttpClient createOkHttpClient(Context context) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        SSLSocketFactory sslSocketFactory = getTrustAllHostsSSLSocketFactory();
        if (sslSocketFactory != null) {
            client.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        }

        try {
            SSLContext sslContext = getSslContextForCertificateFile(context);
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
    private static SSLContext getSslContextForCertificateFile(Context context) {
        try {
            System.setProperty("javax.net.debug", "all");
            KeyStore keyStore = getKeyStore(context);
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

    private static KeyStore getKeyStore(Context context) {
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

    private static SSLSocketFactory getTrustAllHostsSSLSocketFactory() {
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

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
