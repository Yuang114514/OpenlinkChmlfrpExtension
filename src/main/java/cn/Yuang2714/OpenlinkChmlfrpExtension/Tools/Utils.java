package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.util.prefs.BackingStoreException;

public class Utils {
    public static void printExceptionStackTrace(Logger logger, Exception e) {
        StringBuilder builder = new StringBuilder("Stack trace caught!");
        for (StackTraceElement s : e.getStackTrace()) {
            builder.append("\n        ").append(s.toString());
        }
        logger.error(builder.toString());
    }

    public static void flushPreferences(Logger logger, String step) {
        try {
            OpenlinkChmlfrpExtension.PREFERENCES.flush();
        } catch (BackingStoreException e) {
            logger.error("Failed to save in preferences on {} . Exception:{}", step.trim(), e.toString());
            Utils.printExceptionStackTrace(logger, e);
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
            printExceptionStackTrace(OpenlinkChmlfrpExtension.LOGGER, e);
        }
    }
}
