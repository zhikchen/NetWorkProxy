package ipfilter;

import IPModel.IPMessage;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by paranoid on 17-4-21.
 * 测试此Ip是否有效
 */

public class IPUtils {
    public static List<IPMessage> IPIsable(List<IPMessage> ipMessages) {
        String ip;
        String port;

        //CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse response = null;

        for(int i = 0; i < ipMessages.size(); i++) {
            ip = ipMessages.get(i).getIPAddress();
            port = ipMessages.get(i).getIPPort();

            HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
            //.....
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoKeepAlive(false)
                    .setSoLinger(1)
                    .setSoReuseAddress(true)
                    .setSoTimeout(10000)
                    .setTcpNoDelay(true).build();
            
            RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(3000).
                    setSocketTimeout(3000).build();
            //...
            httpClient = HttpClientBuilder.create()
                    .setDefaultSocketConfig(socketConfig)
                    .setDefaultRequestConfig(config).build();

            HttpGet httpGet = new HttpGet("http://www.gov.cn/");
            httpGet.setConfig(config);

            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                    "q=0.9,image/webp,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                    "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

            try {
                response = httpClient.execute(httpGet);
                System.out.println(ipMessages.get(i).getIPAddress() + ": " + ipMessages.get(i).getIPPort());
                System.out.println("此时i："+i+"   此时数组大小"+ipMessages.size());
            } catch (IOException e) {
                out.println("不可用代理已删除" + ipMessages.get(i).getIPAddress() + ": " + ipMessages.get(i).getIPPort());
                ipMessages.remove(ipMessages.get(i));
                i--;
            }
        }

        try {
            httpClient.close();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ipMessages;
    }
}
