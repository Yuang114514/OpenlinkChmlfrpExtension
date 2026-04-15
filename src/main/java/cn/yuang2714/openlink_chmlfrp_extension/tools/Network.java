package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Network {
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String USER_AGENT = "Java/Openlink Chmlfrp Extension/1.0.2";
    static CookieManager manager;

    public static void setUpCookieManager() {
        manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    public static String get(String url, boolean isAuthenticated) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI(url).toASCIIString()).openConnection();
        connection.setRequestMethod("GET");

        if (isAuthenticated) {
            String accessToken;
            accessToken = OpenlinkChmlfrpExtension.PREFERENCES.get("access_token", "UNAUTHED");
            if (accessToken.equals("UNAUTHED")) throw new IllegalArgumentException("Not Authorized");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "application/json");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);
        
        String response = connection.getResponseCode() == 200
                ? new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                : new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        //logger.info("Get Request: {}", response);

        connection.disconnect();

        return response;
    }

    public static String post(String url, @Nullable String body, String contentType, boolean isAuthenticated) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI(url).toASCIIString()).openConnection();

        connection.setRequestMethod("POST");
        if (body != null) connection.setRequestProperty("Content-Type", contentType);

        if (isAuthenticated) {
            String accessToken;
            accessToken = OpenlinkChmlfrpExtension.PREFERENCES.get("access_token", "UNAUTHED");
            if (accessToken.equals("UNAUTHED")) throw new IllegalArgumentException("Not Authorized");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);

        if (body != null) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        String response = connection.getResponseCode() == 200
                ? new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                : new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        connection.disconnect();

        return response;
    }
    
    public static int ping(String domain) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI("http://" + domain).toASCIIString()).openConnection();
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);
        
        long startTime = System.currentTimeMillis();
        
        int responseCode = connection.getResponseCode();
        
        long endTime = System.currentTimeMillis();
        if (responseCode <= 200 || responseCode >= 300) return Integer.MAX_VALUE; //如果响应码不在 200-299 范围内，视为请求失败，返回最大值
        connection.disconnect();
        return Math.toIntExact(endTime - startTime);
    }
}
