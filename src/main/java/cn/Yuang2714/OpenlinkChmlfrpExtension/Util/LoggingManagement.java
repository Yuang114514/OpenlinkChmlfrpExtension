package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonParser;

public class LoggingManagement {
    public static boolean login(String token) {
        if (checkToken(token)) {
            OpenlinkChmlfrpExtension.PREFERENCES.put("token", token);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip", userIsVIP(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named", userHasRealnamed(token));
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china", userIsInChina(token));
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
            return loginState.equals("success");
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Token check failed with an Exception:{}", e.toString());
            return false;
        }
    }

    public static void logout() {
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", false);
        OpenlinkChmlfrpExtension.PREFERENCES.remove("token");
    }

    public static boolean userIsInChina(String token) {
        try {
            String countryCode = JsonParser.parseString(Network.get(URLs.ipCheck))
                    .getAsJsonObject()
                    .get("countryCode")
                    .getAsString();

            OpenlinkChmlfrpExtension.LOGGER.info("User country code:{}", countryCode);
            return countryCode.equals("CN") || countryCode.equals("HK");
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to get your country. Exception:{}", e.toString());
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

            return !userGroup.equals("免费用户");
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to check if you are a VIP, running as you aren't. Exception:{}", e.toString());
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
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to check if you are real named, running as you aren't. Exception:{}", e.toString());
            return false;
        }
    }
}
