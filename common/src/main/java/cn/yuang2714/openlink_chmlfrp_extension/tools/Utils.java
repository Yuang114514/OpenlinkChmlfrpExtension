package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;

public class Utils {
    public static void printExceptionStackTrace(Logger logger, Exception e) {
        StringBuilder builder = new StringBuilder("Stack trace caught!");
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        
        builder.append("\n").append(e);
        for (int i = 0; i < 15; i++) {
            try {
                builder.append("\n        at ").append(stackTraceElements[i].toString());
            } catch (ArrayIndexOutOfBoundsException ignored) {
                logger.error(builder.toString());
                return;
            }
        }
        builder.append("\n").append("... ").append(stackTraceElements.length - 15).append(" More");
        
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
    
    public static Logger genLogger() {
        return LoggerFactory.getLogger(
                OpenlinkChmlfrpExtension.class.getSimpleName() +
                        "/" +
                        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName()
        );
    }
    
    public static double calculateDistance(Location a, Location b) {
        if (a.isImpossible() || b.isImpossible()) return Double.MAX_VALUE;
        
        double latDistance = a.lat - b.lat;
        double lonDistance = a.lon - b.lon;
        return Math.sqrt((latDistance * latDistance) + (lonDistance * lonDistance));
    }
}
