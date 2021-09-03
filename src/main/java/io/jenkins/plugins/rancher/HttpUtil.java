package io.jenkins.plugins.rancher;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by ck on 2020/1/21.
 * <p/>
 */
public class HttpUtil {

    private static String charset = "utf-8";

    public static HttpResponse doPost(String url, String param, String token) {
        PrintWriter out = null;
        BufferedReader in = null;
        String data = "";
        try {
            URL realUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            trustAllHosts(connection);
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("content-type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            if (param != null) {
                out = new PrintWriter(connection.getOutputStream());
                out.print(param);
                out.flush();
            }
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                data += line;
            }
            int statusCode = connection.getResponseCode();
            return new HttpResponse(statusCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {}
        }
        return new HttpResponse(-1, null);
    }

    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};
}
