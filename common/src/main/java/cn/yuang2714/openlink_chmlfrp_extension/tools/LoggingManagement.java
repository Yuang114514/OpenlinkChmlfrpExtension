package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.DeviceCode;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.IntervalledAccessToken;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.TokenIntervalFailedException;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.util.Optional;

public class LoggingManagement {
    static Logger logger = Utils.genLogger();

    public static DeviceCode fetchDeviceCode() throws Exception {
        try {
            JsonObject apiResponse = JsonParser.parseString(Network.post(
                    URLs.oauth2 + "device_authorization",
                    "client_id=" + URLs.clientID + "&scope=offline_access%20chmlfrp_api",
                    Network.CONTENT_TYPE_FORM,
                    false)).getAsJsonObject();
            String deviceCode = apiResponse.get("device_code").getAsString();
            String userCode = apiResponse.get("user_code").getAsString();
            String verificationUriComplete = apiResponse.get("verification_uri_complete").getAsString();
            String expiresIn = String.valueOf(apiResponse.get("expires_in").getAsInt());

            logger.info("Got device code:{}, user code:{}", deviceCode, verificationUriComplete.split("=")[1]);
            if (!deviceCode.isBlank() && !userCode.isBlank() && !verificationUriComplete.isBlank() && !expiresIn.isBlank())
                return new DeviceCode(deviceCode, userCode, verificationUriComplete, expiresIn);
            else throw new NullPointerException("API response is missing required fields.");
        } catch (Exception e) {
            logger.error("Failed to fetch device code.", e);
            //Utils.printExceptionStackTrace(logger, e);
            throw e;
        }
    }

    public static IntervalledAccessToken intervalToken(String deviceCode) throws Exception {
        try {
            JsonObject apiResponse = JsonParser.parseString(Network.post(
                    URLs.oauth2 + "token",
                    "client_id=" +
                            URLs.clientID +
                            "&grant_type=urn:ietf:params:oauth:grant-type:device_code&device_code=" +
                            deviceCode,
                    Network.CONTENT_TYPE_FORM,
                    false)).getAsJsonObject();
            
            if (apiResponse.has("error")) {
                String error = apiResponse.get("error").getAsString();
                switch (error) {
                    case "authorization_pending" ->
                            throw new TokenIntervalFailedException(TokenIntervalFailedException.Cause.AUTHORIZATION_PENDING);
                    case "slow_down" ->
                            throw new TokenIntervalFailedException(TokenIntervalFailedException.Cause.SLOW_DOWN);
                    case "expired_token" ->
                            throw new TokenIntervalFailedException(TokenIntervalFailedException.Cause.EXPIRED_TOKEN);
                    case "access_denied" ->
                            throw new TokenIntervalFailedException(TokenIntervalFailedException.Cause.ACCESS_DENIED);
                    default -> throw new TokenIntervalFailedException(TokenIntervalFailedException.Cause.UNKNOWN);
                }
            }
            String accessToken = apiResponse.get("access_token").getAsString();
            String refreshToken = apiResponse.get("refresh_token").getAsString();
            String expiresIn = String.valueOf(apiResponse.get("expires_in").getAsInt());
            
            if (! accessToken.isBlank() && ! refreshToken.isBlank() && ! expiresIn.isBlank())
                return new IntervalledAccessToken(accessToken, refreshToken, Long.parseLong(expiresIn));
            else throw new NullPointerException("API response is missing required fields.");
        } catch (TokenIntervalFailedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to fetch access token.", e);
            //Utils.printExceptionStackTrace(logger, e);
            throw e;
        }
    }
    
    public static void login(IntervalledAccessToken token) throws Exception {
        OpenlinkChmlfrpExtension.PREFERENCES.put("access_token", token.accessToken());
        OpenlinkChmlfrpExtension.PREFERENCES.put("refresh_token", token.refreshToken());
        OpenlinkChmlfrpExtension.PREFERENCES.putLong("expires_in", System.currentTimeMillis() + token.expiresIn());
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
        Utils.flushPreferences(logger, "logging in");
        refreshUserInfo();
    }

    public static void logout() {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", false);
        OpenlinkChmlfrpExtension.PREFERENCES.remove("access_token");
        OpenlinkChmlfrpExtension.PREFERENCES.remove("refresh_token");
        OpenlinkChmlfrpExtension.PREFERENCES.remove("short_token");
        Utils.flushPreferences(logger, "logging out");
    }
    
    public static void refreshUserInfo() throws Exception {
         JsonObject apiResponseJsonData = JsonParser.parseString(Network.get(
                 URLs.api + "userinfo",
                 true
         )).getAsJsonObject().get("data").getAsJsonObject();
         OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named", apiResponseJsonData.get("realname").getAsString().equals("已实名"));
         OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip", !apiResponseJsonData.get("usergroup").getAsString().equals("免费用户"));
         OpenlinkChmlfrpExtension.PREFERENCES.put("short_token", apiResponseJsonData.get("usertoken").getAsString());
         OpenlinkChmlfrpExtension.PREFERENCES.putInt("max_tunnel_count", apiResponseJsonData.get("tunnel").getAsInt());
         OpenlinkChmlfrpExtension.PREFERENCES.putInt("current_tunnel_count", apiResponseJsonData.get("tunnelCount").getAsInt());
         Utils.flushPreferences(logger, "refreshing user info");
    }
    
    public static void reloadUserAddress() throws Exception {
        JsonObject ipCheckResponse = JsonParser.parseString(Network.get(URLs.ipCheck, false)).getAsJsonObject();
        
        String countryCode = ipCheckResponse.get("countryCode").getAsString();
        logger.info("User country code:{}", countryCode);
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china", countryCode.equals("CN") || countryCode.equals("HK"));
        
        OpenlinkChmlfrpExtension.PREFERENCES.putDouble("lon", ipCheckResponse.get("lon").getAsDouble());
        OpenlinkChmlfrpExtension.PREFERENCES.putDouble("lat", ipCheckResponse.get("lat").getAsDouble());
    }

    public static boolean refreshToken() {
        String refreshToken;
        refreshToken = OpenlinkChmlfrpExtension.PREFERENCES.get("refresh_token", "UNCHECKED");
        if (refreshToken.equals("UNCHECKED")) {
            logger.warn("Failed to get token in preferences.");
            return false;
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

            if (apiResponse.has("error")) {
                logger.error("API returned incorrect message {}.", apiResponse);
                return false;
            }
            else if (
                    !apiResponse.has("access_token")
                    || !apiResponse.has("refresh_token")
                    || !apiResponse.has("expires_in")
            ) return false;

            OpenlinkChmlfrpExtension.PREFERENCES.put("access_token", apiResponse.get("access_token").getAsString());
            OpenlinkChmlfrpExtension.PREFERENCES.put("refresh_token", apiResponse.get("refresh_token").getAsString());
            OpenlinkChmlfrpExtension.PREFERENCES.putLong("expires_in", System.currentTimeMillis() + (apiResponse.get("expires_in").getAsLong() * 1000L) - 60000L);
            return true;
        } catch (Exception e) {
            logger.error("Failed to refresh token.", e);
            //Utils.printExceptionStackTrace(OpenlinkChmlfrpExtension.LOGGER, e);
            return false;
        }
    }
}
