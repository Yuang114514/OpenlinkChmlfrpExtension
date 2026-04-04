package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Network {
    static Logger logger = LogUtils.getLogger();
    static CookieManager manager;

    public static void setUpCookieManager() {
        manager = new CookieManager();
        java.net.CookieHandler.setDefault(manager);
    }

    public static String get(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI(url).toASCIIString()).openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(2000);

        String response = connection.getResponseCode() == 200
                ? new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                : new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        logger.info("Get Request: {}", response);

        connection.disconnect();

        return response;
    }

    public static String post(String url, @Nullable String body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(new URI(url).toASCIIString()).openConnection();
        connection.setRequestMethod("POST");
        if (body != null) connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

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
}
