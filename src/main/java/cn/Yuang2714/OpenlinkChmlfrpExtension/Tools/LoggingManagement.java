package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Arrays;

public class LoggingManagement {
    static Logger logger = LogUtils.getLogger();
    
    public static boolean login(String token) {
        if (checkToken(token)) {
            OpenlinkChmlfrpExtension.PREFERENCES.put("token", token);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip", userIsVIP(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named", userHasRealnamed(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china", userIsInChina());
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
            ExceptionPrinter.printExceptionStackTrace(logger, e);
            logger.error(Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public static void logout() {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", false);
        OpenlinkChmlfrpExtension.PREFERENCES.remove("token");
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
            ExceptionPrinter.printExceptionStackTrace(logger, e);
            logger.error(Arrays.toString(e.getStackTrace()));
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
            ExceptionPrinter.printExceptionStackTrace(logger, e);
            logger.error(Arrays.toString(e.getStackTrace()));
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
            ExceptionPrinter.printExceptionStackTrace(logger, e);
            logger.error(Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public static double[] getUserLocation() {
        try {
            JsonObject ipCheckResponse = JsonParser.parseString(Network.get(URLs.ipCheck)).getAsJsonObject();
            return new double[]{ipCheckResponse.get("lon").getAsDouble(), ipCheckResponse.get("lat").getAsDouble()};
        } catch (Exception e) {
            logger.error("Failed to get your location. Exception:{}", e.toString());
            ExceptionPrinter.printExceptionStackTrace(logger, e);
            logger.error(Arrays.toString(e.getStackTrace()));
            return new double[]{0,0};
        }
    }
}
