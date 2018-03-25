package utilities;

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
        StormLog.info("waiting for statement to be true, waiting for a max of " + maxSeconds + " seconds", StormUtils.class);
        maxSeconds *= 2;
        while (!evaluate.getAsBoolean() && --maxSeconds >= 0) {
            sleep(500);
        }
        return evaluate.getAsBoolean();
    }
}
