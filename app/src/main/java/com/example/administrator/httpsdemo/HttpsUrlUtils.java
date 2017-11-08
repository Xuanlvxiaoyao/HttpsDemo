package com.example.administrator.httpsdemo;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Administrator on 2017/11/7.
 */

public class HttpsUrlUtils {
    //https://www.12306.cn/mormhweb/

    private static HttpsUrlUtils httpsUrlUtils;
    CallBack callBack;
    private HttpsUrlUtils(){

    }

    public static HttpsUrlUtils getInstance(){
        if (httpsUrlUtils==null){
            synchronized (HttpsUrlUtils.class){
                if(httpsUrlUtils==null){
                    httpsUrlUtils=new HttpsUrlUtils();
                }
            }
        }
        return httpsUrlUtils;
    }


    public void getUrlData(final Context context, final String surl, final CallBack callBack){
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   //证书校验
                   InputStream in=context.getAssets().open("srca.cer");
                   CertificateFactory factory=CertificateFactory.getInstance("X.509");
                   Certificate cer=factory.generateCertificate(in);

                   //创建一个包含受信任的CA密钥库
                   KeyStore keyStore=KeyStore.getInstance(KeyStore.getDefaultType());
                   keyStore.load(null);
                   keyStore.setCertificateEntry("ca",cer);

                   //创建一个trustmanager信任的ca密钥库
                   String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                   TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
                   trustManagerFactory.init(keyStore);


                   URL url=new URL(surl);
                   //1.得到https的实例
                   HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();

                   //2.初始化ssl加密
                   SSLContext tls = SSLContext.getInstance("TLS");
                   tls.init(null,trustManagerFactory.getTrustManagers(),new SecureRandom());
                   conn.setSSLSocketFactory(tls.getSocketFactory());
                   conn.setHostnameVerifier(hostnameVerifier);
                   conn.setConnectTimeout(5000);// 设置超时时间
                   InputStream inputStream=conn.getInputStream();

                   byte[] bytes=new byte[1024];
                   StringBuilder sb=new StringBuilder();
                   int i;
                   while ((i=inputStream.read(bytes))!=-1){
                       String s=new String(bytes,0,i);
                       sb.append(s);
                   }

                   String s = sb.toString();

                   callBack.Success(s);
               } catch (Exception e) {
                   e.printStackTrace();
                   callBack.Fail(e);
               }
           }
       }).start();
    }


    HostnameVerifier hostnameVerifier=new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //信任所有主机
            return true;
        }
    };


    interface CallBack{
        void Success(String data);
        void Fail(Exception e);
    }

    public void setListener(CallBack callBack){
        this.callBack=callBack;
    }
}
