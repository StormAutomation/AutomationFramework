package driverBuilder;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utilities.StormLog;
import utilities.StormProperties;

import java.net.MalformedURLException;
import java.net.URL;

import static utilities.StormProperties.getProperty;

public class SeleniumBuilder {

    private static WebDriver drivers;
    private static boolean remote;
    private static String browserName;

    public static WebDriver getBrowserForceNew() {
        drivers = createBrowser();
        return drivers;
    }

    public static WebDriver getBrowser() {
        if (drivers == null) {
            drivers = createBrowser();
        }
        return drivers;
    }

    public static WebDriver createBrowser() {
        WebDriver driver = null;
        remote = Boolean.parseBoolean(getProperty("runRemote"));
        browserName = getProperty("browserName").toLowerCase();
        StormLog.info("Starting up selenium browser driver, " + browserName, SeleniumBuilder.class);
        try {
            StormLog.disableDebugLogging();
            if (remote) {
                driver = getRemoteDriver();
            } else {
                driver = getDriver();
            }
        } catch (Exception e) {
            StormLog.error(e, SeleniumBuilder.class);
        } finally {
            StormLog.enableDebugLogging();
        }
        return driver;
    }
    
    public static boolean isBrowserInitialzied() {
        return drivers != null;
    }

    public static boolean isBrowserClosed(WebDriver browser) {
        SessionId sessionId;
        if (remote) {
            sessionId = ((RemoteWebDriver) browser).getSessionId();
        } else {
            switch (browserName) {
                case "chrome":
                    sessionId = ((ChromeDriver) browser).getSessionId();
                    break;
                case "firefox":
                    sessionId = ((FirefoxDriver) browser).getSessionId();
                    break;
                case "edge":
                    sessionId = ((EdgeDriver) browser).getSessionId();
                    break;
                case "internet explorer":
                    sessionId = ((InternetExplorerDriver) browser).getSessionId();
                    break;
                case "opera":
                    sessionId = ((OperaDriver) browser).getSessionId();
                    break;
                case "safari":
                    sessionId = ((SafariDriver) browser).getSessionId();
                    break;
                default:
                    sessionId = ((InternetExplorerDriver) browser).getSessionId();
            }
        }
        return sessionId == null;
    }

    private static WebDriver getDriver() {
        WebDriver driver = null;

        //get the browser based on what is set in the properties file
        switch (browserName) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                //get the options
                ChromeOptions chromeOptions = getOptions(new ChromeOptions());
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                //get the options
                FirefoxOptions ffOptions = getOptions(new FirefoxOptions());
                driver = new FirefoxDriver(ffOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                //get the options
                EdgeOptions edgeOptions = getOptions(new EdgeOptions());
                driver = new EdgeDriver(edgeOptions);
                break;
            case "internet explorer":
                WebDriverManager.iedriver().setup();
                //get the options
                InternetExplorerOptions ieOptions = getOptions(new InternetExplorerOptions());
                driver = new InternetExplorerDriver(ieOptions);
                break;
            case "safari":
                //get the options
                SafariOptions safariOptions = getOptions(new SafariOptions());
                driver = new SafariDriver(safariOptions);
                break;
            case "opera":
                WebDriverManager.operadriver().setup();
                //get the options
                OperaOptions operaOptions = getOptions(new OperaOptions());
                driver = new OperaDriver(operaOptions);
                break;
        }
        return driver;
    }
    
    private static WebDriver getRemoteDriver() {
        WebDriver driver = null;
        try {
            URL seleniumUrl = new URL(getProperty("remoteUrl"));
            driver = new RemoteWebDriver(seleniumUrl, getCapabilities());
        } catch (MalformedURLException e) {
            StormLog.error(e, SeleniumBuilder.class);
        }
        return driver;
    }

    //capabilities are set in resources/selenium.properties
    private static DesiredCapabilities getCapabilities() {
        String[] options = StormProperties.getPropertyList("DesiredCapabilitiesProperties");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setPlatform(getPlatform());

        //set the rest of the capabilities
        for (String option : options) {
            String property = getProperty(option);
            if (property != null && !property.isEmpty()) {
                if (property.toLowerCase().equals("false") || property.toLowerCase().equals("true")) {
                    capabilities.setCapability(option, Boolean.parseBoolean(property));
                } else {
                    capabilities.setCapability(option, property);
                }
            }
        }

        return capabilities;
    }

    //capabilities are set in resources/selenium.properties
    //this is to set the options for running the driver locally
    private static <T extends MutableCapabilities> T getOptions(MutableCapabilities options) {
        String[] capabilityList = StormProperties.getPropertyList("DesiredCapabilitiesProperties");

        //set the options
        for (String option : capabilityList) {
            String property = getProperty(option);
            if (property != null && !property.isEmpty()) {
                if (property.toLowerCase().equals("false") || property.toLowerCase().equals("true")) {
                    options.setCapability(option, Boolean.parseBoolean(property));
                } else {
                    options.setCapability(option, property);
                }
            }
        }

        return (T)options;
    }

    private static Platform getPlatform() {
        switch (getProperty("platform").toUpperCase()) {
             case "WINDOWS":
                return Platform.WINDOWS;
            case "XP":
                return Platform.XP;
            case "VISTA":
                return Platform.VISTA;
            case "WIN8_1":
                return Platform.WIN8_1;
            case "WIN10":
                return Platform.WIN10;
            case "MAC":
                return Platform.MAC;
            case "SNOW_LEOPARD":
                return Platform.SNOW_LEOPARD;
            case "MOUNTAIN_LION":
                return Platform.MOUNTAIN_LION;
            case "MAVERICKS":
                return Platform.MAVERICKS;
            case "YOSEMITE":
                return Platform.YOSEMITE;
            case "EL_CAPITAN":
                return Platform.EL_CAPITAN;
            case "SIERRA":
                return Platform.SIERRA;
            case "UNIX":
                return Platform.UNIX;
            case "LINUX":
                return Platform.LINUX;
            case "ANDROID":
                return Platform.ANDROID;
            case "IOS":
                return Platform.IOS;
            case "ANY":
                return Platform.ANY;
            default:
                return Platform.ANY;
        }
    }
}
