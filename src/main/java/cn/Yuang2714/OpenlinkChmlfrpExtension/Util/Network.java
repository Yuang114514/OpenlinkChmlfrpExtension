package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import com.google.gson.JsonObject;

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

    public static String post(String url, JsonObject body) throws Exception {
        URI address = new URI(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(address.toASCIIString()).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getAsString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        String response = connection.getResponseCode() == 200 ?
                new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8) :
                new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        connection.disconnect();

        return response;
    }
}
