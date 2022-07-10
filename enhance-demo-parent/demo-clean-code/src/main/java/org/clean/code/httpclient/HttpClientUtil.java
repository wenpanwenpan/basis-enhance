package org.clean.code.httpclient;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * http客户端工具类
 *
 * @author wenpan 2022/05/20 17:42
 */
public class HttpClientUtil {

    private static final HttpClientBuilder HTTP_CLIENT_BUILDER = HttpClients.custom();

    static {
        // 1、绕过不安全的https请求的证书验证
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", trustHttpCertificates()).build();
        // 2、创建连接池
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(registry);
        // 连接池最大50个连接
        pool.setMaxTotal(50);
        // 每个路由默认最大有多少个连接（域名 + 端口 或 ip + 端口相同的就是同一个路由），默认是每个路由最大连接是 <= 2
        pool.setDefaultMaxPerRoute(50);
        HTTP_CLIENT_BUILDER.setConnectionManager(pool);

        // 3、设置请求默认配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(3000)
                .setConnectionRequestTimeout(2000)
                .build();
        HTTP_CLIENT_BUILDER.setDefaultRequestConfig(requestConfig);

        // 4、设置默认的一些header(这里可以按照需求进行添加)
        List<Header> defaultHeaders = new ArrayList<>();
        BasicHeader userAgentHeader = new BasicHeader("user-agent", "xxx");
        defaultHeaders.add(userAgentHeader);

        HTTP_CLIENT_BUILDER.setDefaultHeaders(defaultHeaders);
    }

    /**
     * 发起get请求
     */
    public static String executeGet(String url, Map<String, String> headers) {
        CloseableHttpClient closeableHttpClient = HTTP_CLIENT_BUILDER.build();
        HttpGet httpGet = new HttpGet(url);
        // URL中参数如果有非常规字符参数要进行编码（比如：name="abc+123" 或者 name = "abc def"），不然会导致请求失败
        // 自定义 请求头设置
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpGet.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }
        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            // 关闭响应流
            EntityUtils.consume(httpEntity);
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 发起表单提交post请求
     */
    public static String postForm(String url, List<NameValuePair> list, Map<String, String> headers) {
        CloseableHttpClient closeableHttpClient = HTTP_CLIENT_BUILDER.build();
        HttpPost httpPost = new HttpPost(url);
        // 自定义 请求头设置
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpPost.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }
        // 请求头确保是form
        httpPost.addHeader("Content-Type", "xx");
        // 给post对象设置参数
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            EntityUtils.consume(httpEntity);
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    /**
     * 发起application/json请求
     */
    public static String postJson(String url, String body, Map<String, String> headers) {
        CloseableHttpClient closeableHttpClient = HTTP_CLIENT_BUILDER.build();
        HttpPost httpPost = new HttpPost(url);
        // 自定义 请求头设置
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpPost.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }
        // 请求头确保是json
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        // 给post对象设置json参数
        StringEntity stringEntity = new StringEntity(body, Consts.UTF_8);
        stringEntity.setContentType("application/json; charset=utf-8");
        stringEntity.setContentEncoding(Consts.UTF_8.name());
        httpPost.setEntity(stringEntity);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            // 关闭响应流
            EntityUtils.consume(httpEntity);
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 创建支持安全协议的连接工厂
     */
    private static ConnectionSocketFactory trustHttpCertificates() {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        try {
            sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
                // 判断是否信任url
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            SSLContext sslContext = sslContextBuilder.build();
            return new SSLConnectionSocketFactory(sslContext,
                    // 支持的协议
                    new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    NoopHostnameVerifier.INSTANCE);
        } catch (Exception ex) {
            throw new RuntimeException("ConnectionSocketFactory error.", ex);
        }
    }
}
