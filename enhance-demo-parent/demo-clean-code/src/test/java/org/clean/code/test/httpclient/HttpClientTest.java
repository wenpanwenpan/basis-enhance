package org.clean.code.test.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * http客户端测试
 *
 * @author wenpan 2022/05/20 16:02
 */
public class HttpClientTest {

    /**
     * 下载网络图片并保存到本地
     */
    @Test
    public void test02() throws UnsupportedEncodingException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "图片URL地址";
        // 构造HTTP get请求对象
        HttpGet httpGet = new HttpGet(url);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            // 获取content类型
            Header contentType = httpEntity.getContentType();
            String suffix;
            // 根据contentType决定后缀
            if (contentType.getValue().contains("jpg")) {
                suffix = ".jpg";
            } else if (contentType.getValue().contains("png")) {
                suffix = ".png";
            } else if (contentType.getValue().contains("gif")) {
                suffix = ".gif";
            } else if (contentType.getValue().contains("bmp") || contentType.getValue().contains("bitmap")) {
                suffix = ".bmp";
            } else {
                suffix = ".gif";
            }
            // 图片是二进制形式，则不能直接转为string，所以要转为字节流
            byte[] bytes = EntityUtils.toByteArray(httpEntity);
            String localPath = "/temp/image/abc" + suffix;
            FileOutputStream outputStream = new FileOutputStream(localPath);
            outputStream.write(bytes);
            // 关流
            outputStream.close();

            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置访问代理举例，也就是我们httpClient通过设置ip伪装成其他机器去访问服务器，
     * 因为有些网站会对某些高频访问的IP进行拉黑，所以我们需要使用代理ip来访问，以达到每次访问的IP地址不一样
     */
    @Test
    public void test03() throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "https://www.baidu.com/";
        // 构造HTTP get请求对象
        HttpGet httpGet = new HttpGet(url);
        // 设置请求配置
        // 配置一个代理IP，这里可以自己用免费的玩：66ip.cn，要是生产上用那就需要花钱购买了
        final String ip = "192.168.1.44";
        final int port = 9090;
        HttpHost httpHost = new HttpHost(ip, port);
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(httpHost)
                // 设置连接超时时间（也就是两台机器进行三次握手和成功建立连接的时间）
                .setConnectTimeout(60000)
                // 设置请求超时时间（也就是请求数据读取超时时间）
                .setConnectionRequestTimeout(60000)
                .build();
        httpGet.setConfig(requestConfig);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();

            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 发送post请求(模拟表单参数post请求)
     */
    @Test
    public void test04() throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "https://www.baidu.com/";
        // 构造HTTP get请求对象
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头，post请求如果不设置contentType，则默认是 application/x-www-form-urlencoded;charset-utf-8
        final String contentType = "application/x-www-form-urlencoded;charset-utf-8";
        httpPost.setHeader("Content-Type", contentType);

        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("name", "wenpan"));
        list.add(new BasicNameValuePair("age", "18"));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            System.out.println("httpEntity = " + httpEntity);

            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 发送json格式数据的post请求 content-Type = application/json
     */
    @Test
    public void test05() throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "https://www.baidu.com/";
        // 构造HTTP post 请求对象
        HttpPost httpPost = new HttpPost(url);

        // 构建post请求所需的参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "wenpan");
        jsonObject.put("age", "18");

        StringEntity jsonEntity = new StringEntity(jsonObject.toString(), Consts.UTF_8);
        // 设置请求的ContentType
        jsonEntity.setContentType(new BasicHeader("Content-Type", "application/json;charset=utf-8"));
        // 设置内容编码
        jsonEntity.setContentEncoding(Consts.UTF_8.name());
        // 设置请求的实体
        httpPost.setEntity(jsonEntity);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            System.out.println("httpEntity = " + httpEntity);

            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通过httpClient上传文件(待实现)
     */
    @Test
    public void test06() throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "https://www.baidu.com/";
        // 构造HTTP post 请求对象
        HttpPost httpPost = new HttpPost(url);

        // 构建post请求所需的参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "wenpan");
        jsonObject.put("age", "18");

        StringEntity jsonEntity = new StringEntity(jsonObject.toString(), Consts.UTF_8);
        // 设置请求的ContentType
        jsonEntity.setContentType(new BasicHeader("Content-Type", "application/json;charset=utf-8"));
        // 设置内容编码
        jsonEntity.setContentEncoding(Consts.UTF_8.name());
        // 设置请求的实体
        httpPost.setEntity(jsonEntity);

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            System.out.println("httpEntity = " + httpEntity);

            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 通过httpClient绕过https安全认证，一般有如下两种方案
     * 1、如果有秘钥，则通过认证需要的秘钥来配置四httpClient
     * 2、如果没有秘钥，则配置httpClient绕过https
     */
    @Test
    public void testHttps() throws Exception {

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", trustHttpCertificates()).build();
        // 创建连接池管理器
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(registry);
        // 连接池最大50个连接
        pool.setMaxTotal(50);
        // 每个路由默认最大有多少个连接（域名 + 端口 或 ip + 端口相同的就是同一个路由）
        pool.setDefaultMaxPerRoute(50);
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(pool);
        // 创建http客户端
        CloseableHttpClient httpClient = httpClientBuilder.build();

        final String url = "https://www.baidu.com/";
        HttpGet httpGet = new HttpGet(url);

        // 资源请求和关闭
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            System.out.println("httpEntity = " + httpEntity);
            // 关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 创建支持安全协议的连接工厂
     */
    private ConnectionSocketFactory trustHttpCertificates() throws Exception {

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
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
    }

    @Test
    public void testGet1() throws UnsupportedEncodingException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final String url = "https://www.baidu.com/";
        // 这里使用httpClient发起请求时，请求的参数不会被编码，所以会导致服务端接收参数时出错，所以需要URLEncoder来对参数进行编码
        String password = "123+456 abc|efg";
        password = URLEncoder.encode(password, StandardCharsets.UTF_8.name());
        // 构造HTTP get请求对象
        HttpGet httpGet = new HttpGet(url + password);

        // 解决httpClient被认为不是真人行为，而被拦截问题
        httpGet.setHeader("User-Agent", "");
        // 解决防盗链问题，有的网站为了防止非法获取他的资源会做防盗链，这里的value是：发生防盗链的网站url
        //  httpGet.addHeader("Referer", "xxxxurl");

        // 资源请求和关闭
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            // 代表本次请求成功或失败的状态
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("响应成功.");
            }
            // 获取所有请求头
            Header[] allHeaders = response.getAllHeaders();
            for (Header header : allHeaders) {
                System.out.println("响应头：" + header.getName() + " 的值为： " + header.getValue());
            }
            // HttpEntity 不仅可以作为响应结果，也可以作为请求参数（有非常多的实现）
            HttpEntity httpEntity = response.getEntity();
            // 使用EntityUtils工具类将响应结果转换为字符串
            String string = EntityUtils.toString(httpEntity);
            System.out.println(string);
            // 使用EntityUtils来优雅关闭响应流
            EntityUtils.consume(httpEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
