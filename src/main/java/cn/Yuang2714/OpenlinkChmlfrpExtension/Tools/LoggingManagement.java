package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.prefs.BackingStoreException;

public class LoggingManagement {
    static Logger logger = LogUtils.getLogger();

    public static String[] fetchDeviceCode() throws Exception {
        try {
            JsonObject apiResponse = JsonParser.parseString(Network.post(
                    URLs.oauth2 + "device_authorization",
                    "client_id=" + URLs.clientID + "&scope=offline_access%20chmlfrp_api",
                    Network.CONTENT_TYPE_FORM)).getAsJsonObject();
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
                    Network.CONTENT_TYPE_FORM)).getAsJsonObject();

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
                    case "expired_token" -> {
                        logger.error("Device code expired.");
                        throw new Exception("Device code expired.");
                    }
                    case "access_denied" -> {
                        logger.error("User denied the authorization request.");
                        throw new Exception("User denied the authorization request.");
                    }
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
    
    public static boolean login(String token) throws BackingStoreException {
        if (checkToken(token)) {
            OpenlinkChmlfrpExtension.PREFERENCES.put("token", token);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip", userIsVIP(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named", userHasRealnamed(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china", userIsInChina());
            Utils.flushPreferences(logger, "logging in");
            return true;
        }
        return false;
    }

    public static boolean checkToken(String token) {
        try {
            String loginState = JsonParser.parseString(Network.get(URLs.api + "userinfo?token=" + token))
                    .getAsJsonObject()
                    .get("state")
                    .getAsString();
            logger.info("Logged in.");
            return loginState.equals("success");
        } catch (Exception e) {
            logger.error("Token check failed with an Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return false;
        }
    }

    public static void logout() {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", false);
        OpenlinkChmlfrpExtension.PREFERENCES.remove("token");
        Utils.flushPreferences(logger, "logging out");
    }

    public static boolean userIsInChina() {
        try {
            String countryCode = JsonParser.parseString(Network.get(URLs.ipCheck))
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
            String userGroup = JsonParser.parseString(Network.get(URLs.api + "userinfo?token=" + token))
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
            String realName = JsonParser.parseString(Network.get(URLs.api + "userinfo?token=" + token))
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
            JsonObject ipCheckResponse = JsonParser.parseString(Network.get(URLs.ipCheck)).getAsJsonObject();
            return new double[]{ipCheckResponse.get("lon").getAsDouble(), ipCheckResponse.get("lat").getAsDouble()};
        } catch (Exception e) {
            logger.error("Failed to get your location. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            return new double[]{0,0};
        }
    }
}
