package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
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
}
