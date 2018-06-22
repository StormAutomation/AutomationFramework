package utilities;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.BooleanSupplier;

public class StormUtils {

    public static void sleep(long milliseconds, String message) {
        try {
            StormLog.info("sleeping for " + milliseconds/1000.0 + " seconds " + message, StormUtils.class);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            StormLog.error(e, StormUtils.class);
            e.printStackTrace();
        }
    }

    public static void sleep(long milliseconds) {
        sleep(milliseconds, "");
    }

    static public boolean waitForTrue(BooleanSupplier evaluate) {
        return waitForTrue(evaluate, 5);
    }

    static public boolean waitForTrue(BooleanSupplier evaluate, int maxSeconds) {
        return waitForTrue(evaluate, maxSeconds, 500);
    }
    
    static public boolean waitForTrue(BooleanSupplier evaluate, int maxSeconds, int sleepTime) {
        StormLog.info("waiting for statement to be true, waiting for a max of " + maxSeconds + " seconds", StormUtils.class);
        maxSeconds = (int) (((float)maxSeconds) / (((float)sleepTime)/1000));
        boolean result;
        while (!(result = evaluate.getAsBoolean()) && --maxSeconds >= 0) {
            sleep(sleepTime);
        }
        return result;
    }
    
    static public File getResourceAsFile(String fileName) {
        File file = new File(fileName);
        URL resource = StormUtils.class.getClassLoader().getResource(fileName);
        try {
            FileUtils.copyURLToFile(resource, file);
        } catch (IOException e) {
            StormLog.error(e, StormUtils.class);
            e.printStackTrace();
        }

        return file;
    }
}
