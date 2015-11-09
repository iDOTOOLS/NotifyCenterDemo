package com.idotools.notifycenterdemo.Tools;

import android.util.Log;
import com.google.gson.Gson;
import com.idotools.notifycenterdemo.Model.NotifyRequest;
import com.squareup.okhttp.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;

/**
 * Created by LvWind on 15/10/30.
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();
    static Gson gson = new Gson();


    //okhttp - 网络连接库
    static final OkHttpClient client = getUnsafeOkHttpClient();
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //request 实例
    static NotifyRequest notifyRequest;
    static String requestStirng = gson.toJson(notifyRequest);

    public HttpUtils() {

    }

    public static String postResponse(String url,long lastTimestamp) throws IOException {
        notifyRequest = new NotifyRequest(lastTimestamp);
        requestStirng = gson.toJson(notifyRequest);
        Log.d(TAG,requestStirng);
        RequestBody body = RequestBody.create(JSON, requestStirng);
        final Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();

        if (response.code() != 204) {
            String jsonString = response.body().string();
            response.body().close();
            return jsonString;
        } else {
            response.body().close();
            return null;
        }
    }

    //忽略自签SSL证书错误
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
