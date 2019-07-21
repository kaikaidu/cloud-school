/**
 * Created by Will Zhang on 2018/2/22.
 */

package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

 /**
 * @Description:HttpClient工具类
 * @Date:2018/3/14 16:41
 * @Author:wsc
 */
@Slf4j
public class HttpClientUtil {

   private HttpClientUtil(){
     throw new IllegalAccessError("Utility HttpClientUtil.class");
   }

  /**
   * 绕过验证
   *
   * @return
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException
  {
    SSLContext sc = SSLContext.getInstance("SSLv3");

    // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
    X509TrustManager trustManager = new X509TrustManager() {
      @Override
      public void checkClientTrusted(
          java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
          String paramString) throws CertificateException
      {
      }

      @Override
      public void checkServerTrusted(
          java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
          String paramString) throws CertificateException {
      }

      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }
    };

    sc.init(null, new TrustManager[] { trustManager }, null);
    return sc;
  }

  public static String doPostIgnoreVerifySSL(String url,Object data) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException
  {
    String body = "";
    //采用绕过验证的方式处理https请求
    SSLContext sslcontext = createIgnoreVerifySSL();
    // 设置协议http和https对应的处理socket链接工厂的对象
    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https", new SSLConnectionSocketFactory(sslcontext))
        .build();
    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    HttpClients.custom().setConnectionManager(connManager);

    //创建自定义的httpclient对象
    CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
    //创建post方式请求对象
    HttpPost httpPost = new HttpPost(url);
    String requestParams = JacksonUtil.toJSon(data);
    log.info("requestParams:{}",requestParams);
    httpPost.setHeader("Content-type", "application/json");
    StringEntity entity1 = new StringEntity(requestParams,Charset.forName("UTF-8"));
    entity1.setContentEncoding("UTF-8");
    entity1.setContentType("application/json");
    httpPost.setEntity(entity1);
    //执行请求操作，并拿到结果（）
    CloseableHttpResponse response = client.execute(httpPost);

    //获取结果实体
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      //按指定编码转换结果实体为String类型
      body = EntityUtils.toString(entity);
    }
    //释放链接
    response.close();
    return body;
  }

  public static String doGetIgnoreVerifySSL(String url) throws KeyManagementException, NoSuchAlgorithmException
  {
    String body = "";
    //采用绕过验证的方式处理https请求
    SSLContext sslcontext = createIgnoreVerifySSL();
    // 设置协议http和https对应的处理socket链接工厂的对象
    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https", new SSLConnectionSocketFactory(sslcontext))
        .build();
    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    HttpClients.custom().setConnectionManager(connManager);

    //创建自定义的httpclient对象
    CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
    //创建post方式请求对象
    HttpGet httpGet = new HttpGet(url);
    try{
      HttpEntity entity = client.execute(httpGet).getEntity();
      body = EntityUtils.toString(entity);
    } catch (Exception ex){
      log.info(ex.getMessage());
    }
    return body;
  }

  public static String doGet(String url){
    String body = "";
    CloseableHttpClient httpCilent = HttpClients.createDefault();//Creates CloseableHttpClient instance with default configuration.
    HttpGet httpGet = new HttpGet(url);
    try {
      HttpEntity entity = httpCilent.execute(httpGet).getEntity();
      body = EntityUtils.toString(entity);
    } catch (IOException e) {
      log.error(e.getMessage(),e);
    }finally {
      try {
        httpCilent.close();//释放资源
      } catch (IOException e) {
        log.error(e.getMessage(),e);
      }
    }
    return body;
  }

   /**
    * 获取安利SOA接口信息
    * @param url
    * @param param
    * @return
    */
   public static String sendPost(String url, String param) {
     PrintWriter out = null;
     BufferedReader in = null;
     String result = "";
     try {
       URL realUrl = new URL(url);
       URLConnection conn = realUrl.openConnection();
       conn.setRequestProperty("accept", "*/*");
       conn.setRequestProperty("Accept-Charset", "utf-8");
       conn.setRequestProperty("contentType", "utf-8");
       conn.setRequestProperty("connection", "Keep-Alive");
       conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
       conn.setRequestProperty("user-agent",
           "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
       conn.setDoOutput(true);
       conn.setDoInput(true);
       conn.setConnectTimeout(10000);
       conn.setReadTimeout(10000);
       out = new PrintWriter (conn.getOutputStream());
       out.write(param);
       out.flush();
       in = new BufferedReader(
           new InputStreamReader(conn.getInputStream(),"utf-8"));
       String line;
       while ((line = in.readLine()) != null) {
         result += line;
       }
     } catch (Exception e) {
       log.error(e.getMessage(), e);
     }finally {
       try {
         if (out != null) {
           out.close();
         }
         if (in != null) {
           in.close();
         }
       } catch (IOException ex) {
         log.error(ex.getMessage(), ex);
       }
     }
     return result;
   }

   /**
    * azure blob 获取证书模板html
    * @param path
    * @return
    */
   public static String getJsonByInternet(String path) {
     InputStream is = null;
     ByteArrayOutputStream baos = null;
     try {
       URL url = new URL(path.trim());
       HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
       if (200 == urlConnection.getResponseCode()) {
         is = urlConnection.getInputStream();
         baos = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int len = 0;
         while (-1 != (len = is.read(buffer))) {
           baos.write(buffer, 0, len);
           baos.flush();
         }
         return baos.toString("utf-8");
       }
     } catch (Exception ex) {
       log.error(ex.getMessage(),ex);
     }finally {
       if(null!=is){
         try {
           is.close();
         } catch (IOException ex) {
           log.error(ex.getMessage(),ex);
         }
       }
       if(null!=baos){
         try {
           baos.close();
         } catch (IOException ex) {
           log.error(ex.getMessage(),ex);
         }
       }
     }
     return null;
   }
 }
