package cn.icene.sgkapi.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetUtil {

    public static String doGet(String strUrl) {
        String result = "";
        BufferedReader reader = null;
        String bookJSONString = null;
        try {
            // 1. 建立连接
            HttpURLConnection httpURLConnection = null;
//            String url = "https://v2ray.orzchen.top/api/get-secid.php?code=cl12138";
            String url = strUrl;
            URL requestUrl = new URL(url);

            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();


            // 2. 获取二进制流
            InputStream iniputStream = httpURLConnection.getInputStream();

            // 3. 将二进制流包装
            reader = new BufferedReader((new InputStreamReader(iniputStream)));

            // 4. 从BufferedReader中读取String字符串
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine() )!= null) {
                builder.append(line);
                builder.append("\n");
            }

            if(builder.length() == 0) {
                return null;
            }

            result = builder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
}
