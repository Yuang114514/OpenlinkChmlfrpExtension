package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FrpcManagement {
    static Logger logger = OpenlinkChmlfrpExtension.LOGGER;
    static String[] userEnv = new String[2];
    static Gson gson = new Gson();
    static int[] frpcVersion = new int[3];

    public static void initUserEnv() throws Exception {
        String osArch = System.getProperty("os.arch").toLowerCase(),
                osName = System.getProperty("os.name");

        if (osArch.contains("amd64")) userEnv[1] = "amd64";
        else if (osArch.contains("x86")) userEnv[1] = "386";
        else if (osArch.contains("aarch64")) userEnv[1] = "arm64";
        else if (osArch.contains("arm64")) userEnv[1] = "arm64";
        else {
            logger.error("Unsupported architecture detected!");
            throw new Exception("[OpenLinkChmlfrpExtension] Unsupported architecture detected!");
        }

        if(osName.contains("Windows")) userEnv[0] = "windows";
        else if (osName.contains("OS X")) {
            userEnv[0]="darwin";
            if (osArch.contains("x86_64")) userEnv[1] = "amd64";
        } else if (osName.contains("Linux")||osName.contains("Unix")) userEnv[0] = "linux";
        else if (osName.contains("FreeBSD")) userEnv[0] = "freebsd";
        else {
            logger.error("Unsupported operating system detected!");
            throw new Exception("[OpenLinkChmlfrpExtension] Unsupported operating system detected!");
        }

        logger.info("Got user environment: System:{}, Architecture:{}", userEnv[0], userEnv[1]);
    }

    public static String getCurrentFrpcVersion(Path path) {
        try {
            String version = new String(
                    Runtime.getRuntime().exec(
                            path + "--version"
                            )
                            .getInputStream()
                            .readAllBytes()
                    , StandardCharsets.UTF_8
            ).split("-")[1].split("_")[0];

            frpcVersion = stringVersionToIntArray(version);

            return version;
        } catch (Exception e) {
            logger.error("Failed to check local frpc version. Exception:{}", e.toString());
            return "does not exist";
        }
    }

    private static int[] stringVersionToIntArray(String s) {
        if (s.equals("does not exist")) return new int[] {0, 0, 0};

        String[] gotVersion = s.split("\\.",3);

        int[] version = new int[gotVersion.length];
        for (int i = 0; i < gotVersion.length; i++) {
            version[i] = Integer.parseInt(gotVersion[i]);
        }
        return version;
    }

    private static int[] getLatestFrpcVersion() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URLs.api + "download_info").openConnection();
            connection.setRequestMethod("GET");

            JsonObject response = JsonParser.parseString(new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            return stringVersionToIntArray(response.get("data").getAsJsonObject().get("version").getAsString().split("_")[0]);
        } catch (Exception e) {
            logger.error("Failed to check latest frpc version online. Exception:{}", e.toString());
            return null;
        }
    }

    public static boolean comparateFrpcVersion(Path path) {
        int[] currentVersion = stringVersionToIntArray(getCurrentFrpcVersion(path));

        if (getLatestFrpcVersion() == null) return false;
        int[] latestVersion = getLatestFrpcVersion();

        for (int i = 0; i < 3; i++) {
            int c = currentVersion[i], l = latestVersion[i];
            if (l > c) return true;
        }

        return false;
    }

    public static List<String> getUpdateFileUrls() {
        int[] latestVersion = getLatestFrpcVersion();
        if (getLatestFrpcVersion() == null) return null;

        for (int i = 0; i < 3; i++) {
            int c = frpcVersion[i], l = latestVersion[i];
            if (l > c) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(URLs.api + "download_info/").openConnection();
                    connection.setRequestMethod("GET");

                    JsonObject data = gson.fromJson(
                            connection.getInputStream().toString()
                            , JsonObject.class
                    ).getAsJsonObject("data");

                    String link = data.get("link").getAsString();
                    JsonObject system = data.getAsJsonObject("system");
                    if (system.has(userEnv[0])) {
                        logger.warn("Update file list does not contains your system. Cancelling update.");
                        return null;
                    }

                    JsonArray fileList = system.getAsJsonArray(userEnv[0]);
                    for (int j = 0; j < fileList.size(); j++) {
                        JsonObject fileInfo = fileList.get(j).getAsJsonObject();

                        String forArch = fileInfo.get("architecture").getAsString();
                        if (forArch.equalsIgnoreCase(userEnv[1])) {
                            return List.of(link + fileInfo.get("route").getAsString());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to fetch download link. Exception:{}", e.toString());
                    return null;
                }
            }
        }

        return null;
    }
}
