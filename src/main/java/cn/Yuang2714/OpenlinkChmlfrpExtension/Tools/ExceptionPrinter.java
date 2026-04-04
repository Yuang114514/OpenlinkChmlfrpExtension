package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import org.slf4j.Logger;

public class ExceptionPrinter {
    public static void printExceptionStackTrace(Logger logger, Exception e) {
        StringBuilder builder = new StringBuilder("Stack trace caught!");
        for (StackTraceElement s : e.getStackTrace()) {
            builder.append("\n        ").append(s.toString());
        }
        logger.error(builder.toString());
    }
}
