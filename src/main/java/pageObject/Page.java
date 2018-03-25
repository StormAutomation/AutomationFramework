package pageObject;

import org.openqa.selenium.StaleElementReferenceException;
import utilities.StormLog;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Page {
    private static HashMap<String, Object> instances = new HashMap<>();
    
    protected static <T> T initialize(Class clazz, Object... params) {
        String id = clazz.getName();
        Class[] paramClasses = new Class[params.length];
        for (int i = 0; i < params.length; ++i) {
            paramClasses[i] = params[i].getClass();
        }
        if (!instances.containsKey(id)) {
            try {
                instances.put(id, clazz.getConstructor(paramClasses).newInstance(params));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                StringBuilder debugParam = new StringBuilder();
                for (Object param : params) debugParam.append(" ").append(param);
                StormLog.error("failed to create page object " + clazz.getName() + " using parameters" + debugParam.toString(), clazz);
                StormLog.error(e, clazz);
            }
        }
        return (T)instances.get(id);
    }


    protected void catchStaleElements(Runnable runnable) {
        try {
            runnable.run();
        } catch (StaleElementReferenceException e) {
            StormLog.warn("Stale element exception, trying again", getClass());
            runnable.run();
        }
    }

}
