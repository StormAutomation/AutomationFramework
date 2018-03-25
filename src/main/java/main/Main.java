package main;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import utilities.StormLog;
import utilities.StormProperties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String path = getFullPath(args[0]);
        StormLog.disableDebugLogging();

        switch (StormProperties.getProperty("runParallel").toLowerCase()) {
            case "class":
                args[0] = createTestXmlFileClass(path);
                break;
            case "method":
                args[0] = createTestXmlFileMethod(path);
                break;
        }

        StormLog.enableDebugLogging();

        TestNG.privateMain(args, new TestListener());
    }

    private static String createTestXmlFileMethod(String fullPath) {
        String group = fullPath.split("\\.")[0];
        fullPath = fullPath.substring(group.length() + 1);
        setGroup(group);
        
        String[] splitPath = fullPath.split("\\.");
        String testMethod = splitPath[splitPath.length-1];
        String classPath = fullPath.substring(0, fullPath.length() - (testMethod.length() + 1));
        StormProperties.setProperty("classPath", classPath);
        StormProperties.setProperty("testMethod", testMethod);

        String contents = StormProperties.getProperty("methodXmlFile");

        return writeFile(contents);
    }

    private static String createTestXmlFileClass(String fullPath) {
        String group = fullPath.split("\\.")[0];
        fullPath = fullPath.substring(group.length() + 1);
        setGroup(group);

        StormProperties.setProperty("classPath", fullPath);
        String contents = StormProperties.getProperty("classXmlFile");

        return writeFile(contents);
    }

    private static String writeFile(String contents) {
        File xmlFile = new File("testconfig.xml");
        FileWriter writer;
        try {
            writer = new FileWriter(xmlFile, false);
            writer.write(contents);
            writer.close();
        } catch (IOException e) {
            StormLog.error(e, Main.class);
            e.printStackTrace();
        }

        return xmlFile.getPath();
    }
    
    
    private static void setGroup(String group) {
        if (group.equals(TestFinder.allGroup)) {
            StormProperties.setProperty("testngGroup", "");
        } else {
            StormProperties.setProperty("testngGroup", "<run><include name=\""+group+"\"/></run>");
        }
    }

    private static String getFullPath(String path) {
        return path.replace("/", ".")
                .replace("\\", ".");
    }
}

class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult iTestResult) {
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        StormLog.error(iTestResult.getThrowable(), getClass());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {
    }
}