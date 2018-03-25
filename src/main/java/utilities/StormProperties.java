package utilities;

import org.testng.Assert;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StormProperties {

    protected static boolean skipLogging = false;
    private static Properties propertyReader = readInPropertyFile();
    private static Properties overrideProperties = new Properties();

    public static String getProperty(String key) {
        String value = overrideProperties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        } 
        if (value ==  null) {
            value = System.getenv(key);
        }
        if (value == null) {
            value = propertyReader.getProperty(key);
        }

        if (value == null) {
            log("warn", "property not found " + key);
            return null;
        } else {
            value = buildPartialData(value);
            log("debug", "getting property key: '" + key + "' value: '" + value + "'");
        }
        return value;
    }

    public static String[] getPropertyList(String key) {
        return getProperty(key).split(",");
    }

    public static void setProperty(String key, String value) {
        overrideProperties.setProperty(key, value);
        log("debug","set property key: '" + key + "' value: '" + value + "'");
    }

    public static boolean hasProperty(String key) {
        return overrideProperties.containsKey(key) ||
                System.getenv(key) != null ||
                System.getProperty(key) != null ||
                propertyReader.containsKey(key);
    }

    private static Properties readInPropertyFile() {
        Properties propertyReader = new Properties();
        try {
            //load properties from the default file
            InputStreamReader inputStreamReader = new InputStreamReader(StormProperties.class.getClassLoader().getResourceAsStream("default.properties"), "UTF-8");
            propertyReader.load(inputStreamReader);
            //load propertyReader from all the other files
            for (String fileName : propertyReader.getProperty("additionalPropertyFiles").split(",")) {
                Assert.assertNotNull(StormProperties.class.getClassLoader().getResource(fileName), fileName + " does not exist");
                inputStreamReader = new InputStreamReader(StormProperties.class.getClassLoader().getResourceAsStream(fileName), "UTF-8");
                propertyReader.load(inputStreamReader);
            }
        } catch (IOException e) {
            logError(e);
            e.printStackTrace();
        }
        return propertyReader;
    }

    private static String buildPartialData(String template) {
        while (template.contains("{{") && template.contains("}}")) {
            //use regex matching
            Pattern compile = Pattern.compile("(\\{\\{.*?}})");
            Matcher matcher = compile.matcher(template).usePattern(compile);

            if (matcher.find()) {
                //find all the variables in the string and replace them
                String group = matcher.group(0);
                //need to remove the {{ }} to find the value
                String replaceProperty = getProperty(group.substring(2, group.length() - 2));
                template = template.replace(group, replaceProperty);
            }
        }
        return template;
    }

    private static void log(String type, String msg) {
        if (!skipLogging) {
            switch (type.toLowerCase()) {
                case "debug":
                    StormLog.debug(msg, StormProperties.class);
                    break;
                case "info":
                    StormLog.info(msg, StormProperties.class);
                    break;
                case "warn":
                    StormLog.warn(msg, StormProperties.class);
                    break;
                case "error":
                    StormLog.error(msg, StormProperties.class);
                    break;
            }
        }
    }

    private static void logError(Throwable e) {
        if (!skipLogging) {
            StormLog.error(e, StormProperties.class);
        }
    }
}
