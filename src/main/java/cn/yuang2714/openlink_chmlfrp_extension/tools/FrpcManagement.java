package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrpcManagement {
    static String[] userEnv = new String[2]; //[0]是系统环境，[1]是架构
    static int[] frpcVersion = new int[3];
    static Logger logger = Utils.genLogger();

    public static void initUserEnv() throws Exception {
        //用户信息获取
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
        if (path == null) {
            logger.warn("There is none frpc in storage!");
            return "does not exist";
        }

        try {
            String version = new String(
                    Runtime.getRuntime().exec(
                            path + " --version"
                            )
                            .getInputStream()
                            .readAllBytes()
                    , StandardCharsets.UTF_8
            ).split("-")[1].split("_")[0];

            logger.debug("Got local frpc version: {}", version);
            frpcVersion = stringVersionToIntArray(version);

            return version;
        } catch (Exception e) {
            logger.error("Failed to check local frpc version. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
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

        logger.debug("Converting string version number {} to int array...", s);
        return version;
    }

    private static int[] getLatestFrpcVersion() {
        try {
            JsonObject response = JsonParser.parseString(Network.get(URLs.api + "download_info", false)).getAsJsonObject();
            String version = response.get("data").getAsJsonObject().get("version").getAsString().split("_")[0];
            logger.info("Got remote latest frpc version: {}", version);
            return stringVersionToIntArray(version);
        } catch (Exception e) {
            logger.error("Failed to check latest frpc version online. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return null;
        }
    }

    public static boolean comparateFrpcVersionOnline(Path path) {
        int[] currentVersion = stringVersionToIntArray(getCurrentFrpcVersion(path));

        if (getLatestFrpcVersion() == null) return false;
        int[] latestVersion = getLatestFrpcVersion();
        
        if (comparateFrpcVersion(latestVersion, currentVersion) == 1) {
            logger.info("Found new frpc update! Latest: {}, Current: {}", Arrays.toString(latestVersion), Arrays.toString(currentVersion));
            return true;
        }

        return false;
    }
    
    public static int comparateFrpcVersion(int[] a, int[] b) {
        for (int i = 0; i < 3; i++) {
            int cellInA = a[i], cellInB = b[i];
            if (cellInA == cellInB) continue;
            return cellInA > cellInB ? 1 : -1;
        }
        
        return 0;
    }

    public static List<String> getUpdateFileUrls() {
        List<String> urls = new ArrayList<>();
        
        try {
            JsonObject data = JsonParser.parseString(Network.get(URLs.api + "download_info", false))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonObject();
            
            String link = data.get("link").getAsString();
            
            JsonObject system = data.getAsJsonObject("system");
            if (!system.has(userEnv[0])) {
                logger.error("Update file list does not contains your system. Cancelling update.");
                return null;
            }
            
            JsonArray fileList = system.getAsJsonArray(userEnv[0]);
            for (int i = 0; i < fileList.size(); i++) {
                JsonObject fileInfo = fileList.get(i).getAsJsonObject();
                
                String forArch = fileInfo.get("architecture").getAsString();
                if (forArch.equalsIgnoreCase(userEnv[1])) {
                    urls.add(link + fileInfo.get("route").getAsString());
                }
            }
            
            logger.info("Got update files: {}", Arrays.toString(urls.toArray()));
            return urls;
        } catch (Exception e) {
            logger.error("Failed to fetch download link. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return null;
        }
    }

    public static Process runFrpc(Path path, int proxyId, String token) throws Exception {
        return Runtime.getRuntime().exec(path + " -u " + token + " -p " + proxyId);
    }
}
