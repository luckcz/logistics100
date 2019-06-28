package cn.logistics.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
public class HttpRequestUtil {
        /**
         * 向指定URL发送GET方法的请求
         *
         * @param url
         *            发送请求的URL
         * @param param
         *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
         * @return URL 所代表远程资源的响应结果
         */
        public static String sendGet(String url, String param) {
            String result = "";
            BufferedReader in = null;
            try {
                String urlNameString = url + "?" + param;
                URL realUrl = new URL(urlNameString);
                // 打开和URL之间的连接
                URLConnection connection = realUrl.openConnection();
                // 设置通用的请求属性
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                // 建立实际的连接
                connection.connect();
                // 获取所有响应头字段
                Map<String, List<String>> map = connection.getHeaderFields();
                // 遍历所有的响应头字段
                for (String key : map.keySet()) {
                    System.out.println(key + "--->" + map.get(key));
                }
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } catch (Exception e) {
                System.out.println("发送GET请求出现异常！" + e);
                e.printStackTrace();
            }
            // 使用finally块来关闭输入流
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return result;
        }

        /**
         * 向指定 URL 发送POST方法的请求
         *
         * @param addr
         *            发送请求的 URL
         * @param params
         *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
         * @return 所代表远程资源的响应结果
         */
        public static String post(String addr ,Map<String, String> params) {
            StringBuffer response = new StringBuffer("");

            BufferedReader reader = null;
            try {
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if (builder.length() > 0) {
                        builder.append('&');
                    }
                    builder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    builder.append('=');
                    builder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] bytes = builder.toString().getBytes("UTF-8");

                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(bytes);

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != reader) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return response.toString();
        }
}
