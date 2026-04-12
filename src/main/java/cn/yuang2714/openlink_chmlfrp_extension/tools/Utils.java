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
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        
        builder.append("\n").append(e);
        for (int i = 0; i < 15; i++) {
            try {
                builder.append("\n        ").append(stackTraceElements[i].toString());
            } catch (ArrayIndexOutOfBoundsException ignored) {
                logger.error(builder.toString());
                return;
            }
        }
        builder.append("... ").append(stackTraceElements.length - 15).append(" More");
        
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
