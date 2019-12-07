package com.dalian.httpclient.https;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HttpsIgnoreAll {
    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        CloseableHttpClient client = HttpClients.custom().setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                        .build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        CloseableHttpResponse response = client.execute(new HttpGet("https://www.baidu.com"));
        HttpEntity entity = response.getEntity();
        String enstr = EntityUtils.toString(entity);
        System.out.println(enstr);


    }
}
