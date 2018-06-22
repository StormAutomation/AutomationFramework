package utilities;

import org.joda.time.DateTime;
import org.testng.Reporter;

import java.util.Arrays;

public class StormLog {

    private static boolean debugLogging = true;

    public static void enableDebugLogging() {
        debugLogging = true;
    }

    public static void disableDebugLogging() {
        debugLogging = false;
    }

    public static void info(String msg, Class clazz) {
        if (isLevelLogging("info")) {
            log("info", clazz.getName(), msg);
        }
    }

    public static void debug(String msg, Class clazz) {
        if (debugLogging && isLevelLogging("debug")) {
            log("debug", clazz.getName(), msg);
        }
    }

    public static void warn(String msg, Class clazz) {
        if (isLevelLogging("warn")) {
            log("warn", clazz.getName(), msg);
        }
    }

    public static void error(String msg, Class clazz) {
        if (isLevelLogging("error")) {
            log("error", clazz.getName(), msg);
        }
    }

    public static void error(Throwable e, Class clazz) {
        if (isLevelLogging("error")) {
            log("error", clazz.getName(), e.getClass().getCanonicalName() + ": " + e.getMessage());
            log("error", clazz.getName(), "cause: " + e.getCause());
            StringBuilder builder =  new StringBuilder("Stack Trace:");
            for (StackTraceElement st : e.getStackTrace()) {
                builder.append("\n\t").append(st);
            }
            log("error", clazz.getName(), builder.toString());
        }
    }

    public static void warn(Throwable e, Class clazz) {
        if (isLevelLogging("warn")) {
            log("warn", clazz.getName(), e.getClass().getCanonicalName() + ": " + e.getMessage());
            log("warn", clazz.getName(), "cause: " + e.getCause());
            StringBuilder builder =  new StringBuilder("Stack Trace:");
            for (StackTraceElement st : e.getStackTrace()) {
                builder.append("\n\t").append(st);
            }
            log("warn", clazz.getName(), builder.toString());
        }
    }

    private static String formatMsg (String type, String clazz, String msg) {
        try {
            StormProperties.skipLogging = true;
            String template = StormProperties.getProperty("logStyle");
            String time = DateTime.now().toString(StormProperties.getProperty("logTimeFormat"));
            String testName = Reporter.getCurrentTestResult() == null ? "" : Reporter.getCurrentTestResult().getName();
            return template.replace("[time]", time)
                    .replace("[level]", type.toUpperCase())
                    .replace("[class]", clazz)
                    .replace("[test]", testName)
                    .replace("[message]", msg);
        } finally {
            StormProperties.skipLogging = false;
        }
    }

    private static void log(String type, String clazz, String msg) {
        String l = formatMsg(type, clazz, msg);
        Reporter.log(l);
        System.out.println(l);
    }

    private static boolean isLevelLogging(String level) {
        try {
            StormProperties.skipLogging = true;
            String levels = StormProperties.getProperty("levelsToLog");
            return Arrays.asList(levels.split(",")).contains(level);
        } finally {
            StormProperties.skipLogging = false;
        }
    }
}
