package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Network {
    public static String get(String url) throws Exception {
        URI address = new URI(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(address.toASCIIString()).openConnection();
        connection.setRequestMethod("GET");

        String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        connection.disconnect();

        return response;
    }

    public static String post(String url, @Nullable String body) throws Exception {

        URI address = new URI(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(address.toASCIIString()).openConnection();
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

        String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        connection.disconnect();

        OpenlinkChmlfrpExtension.LOGGER.info("Post request: {}", response);
        return response;
    }
}
