package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonParser;

public class LoggingManagement {
    public static boolean login(String token) {
        if (checkToken(token)) {
            OpenlinkChmlfrpExtension.PREFERENCES.put("token", token);
            OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in", true);
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

    public static boolean userIsInChina() {
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
}
