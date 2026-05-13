package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.platform.PlatformServices;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Network {
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static String USER_AGENT;
    static CookieManager manager;
    private static final Logger logger = Utils.genLogger();

    public static void setUpCookieManager() {
        USER_AGENT = PlatformServices.PLATFORM.genUA();
        manager = new CookieManager();
        CookieHandler.setDefault(manager);
        logger.info("Cookie Manager Set Up. User Agent: {}", USER_AGENT);
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
        
        InputStream responseStream = connection.getInputStream() == null ? connection.getErrorStream() : connection.getInputStream();
        
        String response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        
        //logger.info("Get Request: {}", response);

        connection.disconnect();
        responseStream.close();

        return response;
    }

    public static String post(String url, Optional<String> body, String contentType, boolean isAuthenticated) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI(url).toASCIIString()).openConnection();

        connection.setRequestMethod("POST");
        if (body.isPresent()) connection.setRequestProperty("Content-Type", contentType);

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

        if (body.isPresent()) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.get().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        InputStream responseStream = connection.getInputStream() == null ? connection.getErrorStream() : connection.getInputStream();

        String response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

        connection.disconnect();
        responseStream.close();
        
        //logger.info("Post Request: {}", response);

        return response;
    }
    
    public static int ping(String domain) throws Exception {
        Process process = Runtime.getRuntime().exec("ping " + (FrpcManagement.userEnv[0].contains("windows") ? "-n" : "-c") + " 1 " + domain);
        
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                logger.debug("Ping stdout: {}", line);
                
                int msIndex = line.indexOf("ms");
                if (msIndex == -1) continue;
                
                StringBuilder builder = new StringBuilder();
                for (int i = msIndex - 1; i >= 0; i--) {
                    char cell = line.charAt(i);
                    
                    if (cell == ' ') continue;
                    
                    if (Character.isDigit(cell) || cell == '.') builder.insert(0, cell);
                    else break;
                }
                
                int value = Math.round(Float.parseFloat(builder.toString()));
                logger.debug("Found ms. Parsed to {}", value);
                return value;
            }
        }
        return -1;
    }
}
