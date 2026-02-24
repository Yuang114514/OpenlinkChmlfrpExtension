package cn.Yuang2714.OpenlinkChmlfrpExtension.FrpcUtil;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Openlink_chmlfrp_extension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FrpcManagement {
    static Logger logger = Openlink_chmlfrp_extension.LOGGER;
    static String[] userEnv = new String[2];
    static Gson gson = new Gson();
    static int[] frpcVersion = new int[3];

    public static void initUserEnv() throws Exception {
        String osArch = System.getProperty("os.arch").toLowerCase(),
                osName = System.getProperty("os.name");

        if(osArch.contains("i386")){
            osArch="386";
        }

        if(osName.contains("Windows")) {
            userEnv[0]="windows";
        } else if (osName.contains("OS X")) {
            userEnv[0]="darwin";
            osArch = osArch.equals("x86_64") ? "amd64" : "arm64";
        } else if (osName.contains("Linux")||osName.contains("Unix")) {
            userEnv[0] = "linux";
        } else if (osName.contains("FreeBSD")){
            userEnv[0] = "freebsd";
        } else {
            logger.error("Unsupported operating system detected!");
            throw new Exception("[OpenLinkChmlfrpExtension] Unsupported operating system detected!");
        }
        userEnv[1] = osArch;
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
            //logger.error("Failed to check frpc version. Exception:{}", e.toString());
            return "does not exists";
        }
    }

    private static int[] stringVersionToIntArray(String s) {
        String[] gotVersion = s.split("\\.",3);

        int[] version = new int[gotVersion.length];
        for (int i = 0; i < gotVersion.length; i++) {
            version[i] = Integer.parseInt(gotVersion[i]);
        }
        return version;
    }

    private static int[] getLatestFrpcVersion() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URLs.api + "download_info/").openConnection();
            connection.setRequestMethod("GET");

            JsonObject response = gson.fromJson(connection.getInputStream().toString(), JsonObject.class);
            return stringVersionToIntArray(response.get("version").getAsString().split("_")[0]);
        } catch (Exception e) {
            logger.error("Failed to check latest frpc version. Exception:{}", e.toString());
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
}
