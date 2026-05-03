package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    
//    public static void printExceptionStackTrace(Logger logger, Exception e) {
//        StringBuilder builder = new StringBuilder("Stack trace caught! \n").append(e);
//
//        StackTraceElement[] stackTraceElements = e.getStackTrace();
//        for (int i = 0; i < 15; i++) {
//            try {
//                builder.append("\n        at ").append(stackTraceElements[i].toString());
//            } catch (ArrayIndexOutOfBoundsException ignored) {
//                logger.error(builder.toString());
//                return;
//            }
//        }
//        builder.append("\n").append("        ... ").append(stackTraceElements.length - 15).append(" More");
//
//        logger.error(builder.toString());
//    }

    public static boolean flushPreferences(Logger logger, String step) {
        try {
            OpenlinkChmlfrpExtension.PREFERENCES.flush();
            return true;
        } catch (Exception e) {
            logger.error("Failed to save in preferences on {} . Exception:{}", step.trim(), e);
            //printExceptionStackTrace(logger, e);
            return false;
        }
    }
    
    public static Logger genLogger() {
        return LoggerFactory.getLogger(
                OpenlinkChmlfrpExtension.class.getSimpleName() +
                        "/" +
                        walker.getCallerClass().getSimpleName()
        );
    }
    
    public static double calculateDistance(Location a, Location b) {
        if (a.isImpossible() || b.isImpossible()) return Double.MAX_VALUE;
        
        double latDistance = a.lat - b.lat;
        double lonDistance = a.lon - b.lon;
        return Math.sqrt((latDistance * latDistance) + (lonDistance * lonDistance));
    }
}
