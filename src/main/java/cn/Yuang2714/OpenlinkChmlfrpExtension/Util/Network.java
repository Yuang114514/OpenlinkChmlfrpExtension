package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Network {
    public static String get(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        connection.disconnect();

        return response;
    }
}
