package main;

import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestFinder {

    private static String fileName = "tests.txt";
    private static URLClassLoader loader;
    private static String jarPath;

    public static final String allGroup = "AllTests";

    /**
     * find the tests in the repo
     */
    public static void main(String[] args) {
        jarPath = args[0];
        File file = new File(fileName);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            URL url = new File(jarPath).toURI().toURL();
            loader = new URLClassLoader(new URL[]{url}, TestFinder.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            findClassesJar(new File(jarPath));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static void findTests(String clazz) {
        try {
            Class classToLoad = Class.forName(clazz, false, loader);
            for (Method m : classToLoad.getMethods()) {
                List<Annotation> annotations = new ArrayList<>();
                annotations.addAll(Arrays.asList(m.getDeclaredAnnotations()));
                annotations.removeIf(a -> !a.annotationType().equals(Test.class));
                if (annotations.size() > 0) {
                    writeFile(allGroup + "." + clazz + "." + m.getName());
                    for (String group : m.getAnnotation(Test.class).groups())
                        if (!group.equals(allGroup)) {
                            writeFile(group + "." + clazz + "." + m.getName());
                        }
                }
            }
        } catch(ClassNotFoundException|NoClassDefFoundError e) {
//            e.printStackTrace();
        }
    }

    private static void writeFile(String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))){
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean findClassesJar(File file) {
        if (file.getName().toLowerCase().endsWith(".jar")) {
            JarFile jar = null;
            try {
                jar = new JarFile(file);
            } catch (Exception ex) {

            }
            if (jar != null) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    String name = entry.getName();
                    int extIndex = name.lastIndexOf(".class");
                    if (extIndex > 0) {
                        findTests(name.substring(0, extIndex).replace("/", "."));
                    }
                }
            }
        }
        return true;
    }
}