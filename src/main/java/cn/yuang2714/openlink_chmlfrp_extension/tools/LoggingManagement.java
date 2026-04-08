package cn.yuang2714.openlink_chmlfrp_extension.tools;

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class LoggingManagement {
    static Logger logger = LogUtils.getLogger();

    public static String[] fetchDeviceCode() throws Exception {
        try {
            JsonObject apiResponse = JsonParser.parseString(Network.post(
                    URLs.oauth2 + "device_authorization",
                    "client_id=" + URLs.clientID + "&scope=offline_access%20chmlfrp_api",
                    Network.CONTENT_TYPE_FORM,
                    false)).getAsJsonObject();
            String deviceCode = apiResponse.get("device_code").getAsString();
            String verificationUriComplete = apiResponse.get("verification_uri_complete").getAsString();
            String expiresIn = String.valueOf(apiResponse.get("expires_in").getAsInt());

            logger.info("Got device code:{}, user code:{}", deviceCode, verificationUriComplete.split("=")[1]);
            if (!deviceCode.isBlank() && !verificationUriComplete.isBlank() && !expiresIn.isBlank())
                return new String[]{deviceCode, verificationUriComplete, expiresIn};
            else throw new NullPointerException("API response is missing required fields.");
        } catch (Exception e) {
            logger.error("Failed to fetch device code. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            throw e;
        }
    }

    public static String[] intervalToken(String deviceCode) throws Exception {
        try {
            JsonObject apiResponse = JsonParser.parseString(Network.post(
                    URLs.oauth2 + "token",
                    "client_id=" + URLs.clientID + "&grant_type=urn:ietf:params:oauth:grant-type:device_code&device_code=" + deviceCode,
                    Network.CONTENT_TYPE_FORM,
                    false)).getAsJsonObject();

            if (apiResponse.has("error")) {
                String error = apiResponse.get("error").getAsString();
                switch (error) {
                    case "authorization_pending" -> {
                        logger.info("Authorization pending, waiting for user to authorize...");
                        return new String[]{"authorization_pending"};
                    }
                    case "slow_down" -> {
                        logger.warn("Polling too frequently, slowing down...");
                        return new String[]{"slow_down"};
                    }
                    case "expired_token" -> throw new Exception("Device code expired.");
                    case "access_denied" -> throw new Exception("User denied the authorization request.");
                    default -> throw new Exception("Unknown Error:" + error);
                }
            }
            String accessToken = apiResponse.get("access_token").getAsString();
            String refreshToken = apiResponse.get("refresh_token").getAsString();
            String expiresIn = String.valueOf(apiResponse.get("expires_in").getAsInt());

            if (!accessToken.isBlank() && !refreshToken.isBlank() && !expiresIn.isBlank())
                return new String[]{accessToken, refreshToken, expiresIn};
            else throw new NullPointerException("API response is missing required fields.");
        } catch (Exception e) {
            logger.error("Failed to fetch access token. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            throw e;
        }
    }
    
    public static void login(String accessToken, String refreshToken, int expiresIn) {
        OpenlinkChmlfrpExtension.PREFERENCES.put("access_token", accessToken);
        OpenlinkChmlfrpExtension.PREFERENCES.put("refresh_token", refreshToken);
        OpenlinkChmlfrpExtension.PREFERENCES.putLong("expires_in", (System.currentTimeMillis() / 1000) + expiresIn - 60);
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip", userIsVIP(accessToken));
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named", userHasRealnamed(accessToken));
        Utils.flushPreferences(logger, "logging in");
    }

    public static void logout() {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", false);
        OpenlinkChmlfrpExtension.PREFERENCES.remove("access_token");
        OpenlinkChmlfrpExtension.PREFERENCES.remove("refresh_token");
        Utils.flushPreferences(logger, "logging out");
    }

    public static boolean userIsInChina() {
        try {
            String countryCode = JsonParser.parseString(Network.get(URLs.ipCheck, false))
                    .getAsJsonObject()
                    .get("countryCode")
                    .getAsString();

            logger.info("User country code:{}", countryCode);
            return countryCode.equals("CN") || countryCode.equals("HK");
        } catch (Exception e) {
            logger.error("Failed to get your country. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return true;
        }
    }

    public static boolean userIsVIP(String token) {
        try {
            String userGroup = JsonParser.parseString(Network.get(URLs.api + "userinfo?token=" + token, true))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonObject()
                    .get("usergroup")
                    .getAsString();
            logger.info("Checking if you are VIP. API Response:{}", userGroup);

            return !userGroup.equals("免费用户");
        } catch (Exception e) {
            logger.error("Failed to check if you are a VIP, running as you aren't. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return false;
        }
    }


    public static boolean userHasRealnamed(String token) {
        try {
            String realName = JsonParser.parseString(Network.get(URLs.api + "userinfo?token=" + token, false))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonObject()
                    .get("realname")
                    .getAsString();

            return realName.equals("已实名");
        } catch (Exception e) {
            logger.error("Failed to check if you are real named, running as you aren't. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return false;
        }
    }

    public static double[] getUserLocation() {
        try {
            JsonObject ipCheckResponse = JsonParser.parseString(Network.get(URLs.ipCheck, false)).getAsJsonObject();
            return new double[]{ipCheckResponse.get("lon").getAsDouble(), ipCheckResponse.get("lat").getAsDouble()};
        } catch (Exception e) {
            logger.error("Failed to get your location. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return new double[]{0,0};
        }
    }

    public static void refreshToken() {
        String refreshToken;
        refreshToken = OpenlinkChmlfrpExtension.PREFERENCES.get("refresh_token", "UNCHECKED");
        if (refreshToken.equals("UNCHECKED")) {
            OpenlinkChmlfrpExtension.PREFERENCES.putInt("expires_in", 0);
            return;
        }

        try {
            JsonObject apiResponse = JsonParser.parseString(
                    Network.post(
                            URLs.oauth2 + "token",
                            "grant_type=refresh_token&client_id=" + URLs.clientID + "&refresh_token=" + refreshToken,
                            Network.CONTENT_TYPE_FORM,
                            false
                    )
            ).getAsJsonObject();
            if (
                    apiResponse.has("error")//todo: fuck mother
            );
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to refresh token. {}", e.toString());
            Utils.printExceptionStackTrace(OpenlinkChmlfrpExtension.LOGGER, e);
        }
    }
}
