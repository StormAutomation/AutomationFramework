package pageObject.webPage;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObject.Page;
import utilities.StormLog;
import utilities.StormUtils;
import utilities.api.ApiResponse;
import driverBuilder.SeleniumBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebPage extends Page {

    private WebDriver browser;
    private int apiTryCount = 100;
    private boolean boolResult;
    private String stringResult;
    private Dimension dimensionResult;
    private Rectangle rectangleResult;
    private Point pointResult;
    private WebElement elementResult;
    private List<WebElement> elementListResult;

    public void setBrowser(WebDriver browser) {
        this.browser = browser;
    }

    public WebDriver getBrowser() {
        if (browser == null) {
            browser = SeleniumBuilder.getBrowser();
        } else if (SeleniumBuilder.isBrowserClosed(browser)) {
            browser = SeleniumBuilder.getBrowserForceNew();
        }
        return browser;
    }

    protected void navigateTo(String url) {
        StormLog.info("navigating to " + url, getClass());
        getBrowser().navigate().to(url);
        StormUtils.waitForTrue(()->isCurrentUrl(url));
        waitForPageLoad();        
    }

    private boolean isCurrentUrl(String url) {
        String browserUrl = getBrowser().getCurrentUrl();
        if (browserUrl.endsWith("/") && !url.endsWith("/")) {
            return browserUrl.equals(url.toLowerCase() + "/");
        } else if (!browserUrl.endsWith("/") && url.endsWith("/")) {
            return url.toLowerCase().equals(browserUrl + "/");
        }
        return browserUrl.equals(url.toLowerCase());
    }

    protected void navigateBack() {
        StormLog.info("navigating back a page", getClass());
        getBrowser().navigate().back();
        waitForPageLoad();
    }

    protected void navigateForward() {
        StormLog.info("navigating forward a page", getClass());
        getBrowser().navigate().forward();
        waitForPageLoad();
    }

    protected void refreshPage() {
        StormLog.info("refreshing page", getClass());
        getBrowser().navigate().refresh();
        waitForPageLoad();
    }
    
    public void waitForPageLoad() {
        waitForPageLoad(10);
    }
    
    public void waitForPageLoad(int maxSeconds) {
        StormLog.info("waiting for the page to load", getClass());
        String javascript = "return document.readyState === 'complete'";
        int retry = maxSeconds * 2;
        boolean loaded = false;
        while (!loaded && retry-- > 0) {
            loaded = (boolean) ((JavascriptExecutor)getBrowser()).executeScript(javascript);
            StormUtils.sleep(500, "waiting for the page to load");
        }
    }

    protected void click(By identifier) {
        StormLog.info("clicking on element: " + identifier.toString(), getClass());
        catchStaleElements(()->{
            getBrowser().findElement(identifier).click();
        });
    }
    
    protected void click(WebElement element) {
        StormLog.info("clicking on element: " + element.getTagName(), getClass());
        element.click();
    }

    protected void sendKeys(By identifier, String keys) {
        StormLog.info("sending keys '" + keys + "' to element: " + identifier.toString(), getClass());
        catchStaleElements(()->{
            getBrowser().findElement(identifier).sendKeys(keys);
        });
    }
    
    protected void sendKeys(WebElement element, String keys) {
        StormLog.info("sending keys '" + keys + "' to element: " + element.getTagName(), getClass());
        element.sendKeys(keys);
    }

    //send a string or org.openqa.selenium.Keys for custom keys
    protected void sendKeys(CharSequence... keys) {
        StringBuilder keyLog = new StringBuilder();
        String seperator = "";
        for (CharSequence key : keys) {
            keyLog.append(seperator);
            keyLog.append(key.toString());
            seperator = ",";
        }
        StormLog.info("sending keys '" + keyLog + "' to browser",getClass());
        new Actions(getBrowser()).sendKeys(keys).build().perform();
    }

    protected WebElement findElement(By identifier) {
        StormLog.info("finding element: " + identifier, getClass());
        elementResult = null;
        catchStaleElements(()->{
            elementResult = getBrowser().findElement(identifier);
        });
        return elementResult;
    }

    protected List<WebElement> findElements(By identifier) {
        StormLog.info("finding element: " + identifier, getClass());
        elementListResult = null;
        catchStaleElements(()->{
            elementListResult = getBrowser().findElements(identifier);
        });
        return elementListResult;
    }
    
    protected void scrollToTopOfPage() {
        StormLog.info("scrolling to the top of the page", getClass());
        ((JavascriptExecutor) getBrowser()).executeScript("window.scrollTo(0, 0);");
    }
    
    protected void scrollToElement(By identifier) {
        StormLog.info("scrolling to element: " + identifier, getClass());
        catchStaleElements(()->{
            ((JavascriptExecutor) getBrowser()).executeScript("arguments[0].scrollIntoView(true);", getBrowser().findElement(identifier));
        });
    }
    
    protected void scrollToElement(WebElement element) {
        StormLog.info("scrolling to element: " + element.getTagName(), getClass());
        ((JavascriptExecutor) getBrowser()).executeScript("arguments[0].scrollIntoView(true);", element);        
    }
    
    protected void scrollUp() {
        StormLog.info("scrolling up", getClass());
        new Actions(getBrowser()).sendKeys(Keys.ARROW_UP).build().perform();
    }
    
    protected void scrollDown() {
        StormLog.info("scrolling down", getClass());
        new Actions(getBrowser()).sendKeys(Keys.ARROW_DOWN).build().perform();        
    }

    protected String getCurrentUrl() {
        String currentUrl = getBrowser().getCurrentUrl();
        StormLog.info("current url: " + currentUrl, getClass());
        return currentUrl;
    }
    
    protected boolean waitForPageUrlToContain(String url) {
        StormLog.info("Waiting for the url to contain " + url, getClass());
        StormUtils.waitForTrue(()->getBrowser().getCurrentUrl().contains(url));
        return getBrowser().getCurrentUrl().contains(url);
    }

    protected boolean waitForPageUrlToEndWith(String url) {
        StormLog.info("Waiting for the url to end with " + url, getClass());
        StormUtils.waitForTrue(()->getBrowser().getCurrentUrl().endsWith(url));
        return getBrowser().getCurrentUrl().endsWith(url);
    }

    protected boolean waitForPageUrlToStartWith(String url) {
        StormLog.info("Waiting for the url to start with " + url, getClass());
        StormUtils.waitForTrue(()->getBrowser().getCurrentUrl().startsWith(url));
        return getBrowser().getCurrentUrl().startsWith(url);
    }

    protected String getPageSource() {
        StormLog.info("getting page source", getClass());
        return getBrowser().getPageSource();
    }

    protected String getTitle() {
        StormLog.info("getting title", getClass());
        return getBrowser().getTitle();
    }

    protected void selectByVisibleText(By identifier, String text) {
        StormLog.info("selecting " + text + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).selectByVisibleText(text);
        });
    }

    protected void selectByIndex(By identifier, int index) {
        StormLog.info("selecting " + index + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).selectByIndex(index);
        });
    }

    protected void selectByValue(By identifier, String value) {
        StormLog.info("selecting " + value + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).selectByValue(value);
        });
    }

    protected void deselectByIndex(By identifier, int index) {
        StormLog.info("deselecting " + index + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).deselectByIndex(index);
        });
    }

    protected void deselectByValue(By identifier, String value) {
        StormLog.info("deselecting " + value + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).deselectByValue(value);
        });
    }

    protected void deselectByVisibleText(By identifier, String text) {
        StormLog.info("deselecting " + text + " for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).deselectByVisibleText(text);
        });
    }

    protected List<WebElement> getOptions(By identifier) {
        StormLog.info("get options for " + identifier.toString(), getClass());
        elementListResult = null;
        catchStaleElements(()->{
            elementListResult = new Select(getBrowser().findElement(identifier)).getOptions();
        });
        return elementListResult;
    }

    protected void deselectAll(By identifier) {
        StormLog.info("deselecting all for element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Select(getBrowser().findElement(identifier)).deselectAll();
        });
    }
    
    protected void selectByVisibleText(WebElement element, String text) {
        StormLog.info("selecting " + text + " for element " + element.getTagName(), getClass());
        new Select(element).selectByVisibleText(text);
    }

    protected void selectByIndex(WebElement element, int index) {
        StormLog.info("selecting " + index + " for element " + element.getTagName(), getClass());
        new Select(element).selectByIndex(index);
    }

    protected void selectByValue(WebElement element, String value) {
        StormLog.info("selecting " + value + " for element " + element.getTagName(), getClass());
        new Select(element).selectByValue(value);
    }

    protected void deselectByIndex(WebElement element, int index) {
        StormLog.info("deselecting " + index + " for element " + element.getTagName(), getClass());
        new Select(element).deselectByIndex(index);
    }

    protected void deselectByValue(WebElement element, String value) {
        StormLog.info("deselecting " + value + " for element " + element.getTagName(), getClass());
        new Select(element).deselectByValue(value);
    }

    protected void deselectByVisibleText(WebElement element, String text) {
        StormLog.info("deselecting " + text + " for element " + element.getTagName(), getClass());
        new Select(element).deselectByVisibleText(text);
    }

    protected List<WebElement> getOptions(WebElement element) {
        StormLog.info("get options for " + element.getTagName(), getClass());
        return new Select(element).getOptions();
    }

    protected void deselectAll(WebElement element) {
        StormLog.info("deselecting all for element " + element.getTagName(), getClass());
        new Select(element).deselectAll();
    }

    protected <T> T webDriverWaitUntil(ExpectedCondition<T> waitMethod, int timeoutInSeconds) {
        StormLog.info("web getDriver() wait until, timeout seconds: " + timeoutInSeconds, getClass());
        return new WebDriverWait(getBrowser(), timeoutInSeconds).until(waitMethod);
    }

    protected <T> void webDriverWaitUntil(ExpectedCondition<T> waitMethod) {
        webDriverWaitUntil(waitMethod, 5);
    }

    protected String getWindowHandle() {
        String windowHandle = getBrowser().getWindowHandle();
        StormLog.info("window handle: " + windowHandle, getClass());
        return windowHandle;
    }

    protected Set<String> getWindowHandles() {
        Set<String> windowHandles = getBrowser().getWindowHandles();
        StringBuilder logText = new StringBuilder();
        String seperator = "";
        for (String handle : windowHandles) {
            logText.append(seperator);
            logText.append(handle);
            seperator = ",";
        }
        StormLog.info("window handles: " + logText, getClass());
        return windowHandles;
    }

    protected Alert switchToAlert() {
        StormLog.info("switching to alert", getClass());
        return getBrowser().switchTo().alert();
    }

    protected void switchToWindow(String windowHandle) {
        StormLog.info("switching to window: " + windowHandle, getClass());
        getBrowser().switchTo().window(windowHandle);
    }

    protected void switchToFrame(WebElement frame) {
        StormLog.info("switching to frame: " + frame, getClass());
        getBrowser().switchTo().frame(frame);
    }

    protected void switchToFrame(int frame) {
        StormLog.info("switching to frame: " + frame, getClass());
        getBrowser().switchTo().frame(frame);
    }

    protected void switchToFrame(String frame) {
        StormLog.info("switching to frame: " + frame, getClass());
        getBrowser().switchTo().frame(frame);
    }

    protected WebElement switchToActiveElement() {
        StormLog.info("switching to active element", getClass());
        return getBrowser().switchTo().activeElement();
    }

    protected void switchToDefaultContent() {
        StormLog.info("switching to default content", getClass());
        getBrowser().switchTo().defaultContent();
    }

    public void switchToParentFrame() {
        StormLog.info("switching to parent frame", getClass());
        getBrowser().switchTo().parentFrame();
    }

    public void switchToIframe() {
        StormLog.info("switching to iframe", getClass());
        WebElement iframe = getBrowser().findElement(By.tagName("iframe"));
        getBrowser().switchTo().frame(iframe);
    }

    protected void switchToTab(int index) {
        ArrayList<String> tabs = new ArrayList<>(getBrowser().getWindowHandles());
        StormLog.info("switching to window: " + tabs.get(index), getClass());
        switchToWindow(tabs.get(index));
    }

    protected void keyDown(Keys key) {
        StormLog.info("key down " + key.name(), getClass());
        new Actions(getBrowser()).keyDown(key).build().perform();
    }

    protected void keyUp(Keys key) {
        StormLog.info("key up " + key.name(), getClass());
        new Actions(getBrowser()).keyUp(key).build().perform();
    }

    protected void clickAndHold(By identifier) {
        StormLog.info("click and hold " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).clickAndHold(getBrowser().findElement(identifier)).build().perform();
        });
    }
    
    protected void clickOffset(By identifier, int xOffset, int yOffset) {
        StormLog.info("click offset by X "+xOffset+" and Y "+yOffset+" of element: " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).moveToElement(getBrowser().findElement(identifier), xOffset, yOffset).click().build().perform();
        });
    }
    
    protected void clickAndHold(WebElement element) {
        StormLog.info("click and hold " + element.getTagName(), getClass());
        new Actions(getBrowser()).clickAndHold(element).build().perform();
    }
    
    protected void clickOffset(WebElement element, int xOffset, int yOffset) {
        StormLog.info("click offset by X "+xOffset+" and Y "+yOffset+" of element: " + element.getTagName(), getClass());
        new Actions(getBrowser()).moveToElement(element, xOffset, yOffset).click().build().perform();
    }
    
    protected void keyDown(CharSequence chars) {
        StormLog.info("key down " + chars, getClass());
        new Actions(getBrowser()).keyDown(chars).build().perform();
    }
    
    protected void keyDown(By identifier, CharSequence chars) {
        StormLog.info("key down " + chars + " on element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).keyDown(getBrowser().findElement(identifier), chars).build().perform();
        });
    }
    
    protected void keyDown(WebElement element, CharSequence chars) {
        StormLog.info("key down " + chars + " on element " + element.getTagName(), getClass());
        new Actions(getBrowser()).keyDown(element, chars).build().perform();
    }
    
    protected void KeyUp(CharSequence chars) {
        StormLog.info("key up " + chars, getClass());
        new Actions(getBrowser()).keyUp(chars).build().perform();
    }
    
    protected void keyUp(By identifier, CharSequence chars) {
        StormLog.info("key up " + chars + " on element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).keyUp(getBrowser().findElement(identifier), chars).build().perform();
        });
    }
    
    protected void moveTo(By identifier) {
        StormLog.info("moving to element " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).moveToElement(getBrowser().findElement(identifier)).build().perform();
        });
    }
    
    protected void moveToAndClick(By identifier) {
        StormLog.info("moving to element " + identifier.toString() + " and clicking", getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).moveToElement(getBrowser().findElement(identifier)).click().build().perform();
        });
    }
    
    protected void keyUp(WebElement element, CharSequence chars) {
        StormLog.info("key up " + chars + " on element " + element.getTagName(), getClass());
        new Actions(getBrowser()).keyUp(element, chars).build().perform();
    }
    
    protected void moveTo(WebElement element) {
        StormLog.info("moving to element " + element.getTagName(), getClass());
        new Actions(getBrowser()).moveToElement(element).build().perform();
    }
    
    protected void moveToAndClick(WebElement element) {
        StormLog.info("moving to element " + element.getTagName() + " and clicking", getClass());
        new Actions(getBrowser()).moveToElement(element).click().build().perform();
    }
    
    protected void releaseClick() {
        StormLog.info("releasing click", getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).release().build().perform();
        });
    }

    protected void doubleClick(By identifier) {
        StormLog.info("double click " + identifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).doubleClick(getBrowser().findElement(identifier)).build().perform();
        });
    }

    protected void dragAndDrop(By startIdentifier, By endIdentifier) {
        StormLog.info("drag and dropping from " + startIdentifier.toString() + " to " + endIdentifier.toString(), getClass());
        catchStaleElements(()->{
            new Actions(getBrowser()).dragAndDrop(getBrowser().findElement(startIdentifier), getBrowser().findElement(endIdentifier)).build().perform();
        });
    }

    protected void clear(By identifier) {
        StormLog.info("clearing field " + identifier.toString(), getClass());
        catchStaleElements(()->{
            getBrowser().findElement(identifier).clear();            
        });
    }
    
    protected String getAttribute(By identifier, String attribute) {
        StormLog.info("getting attribute '"+attribute+"' from '"+identifier.toString()+"'", getClass());
        stringResult = "";
        catchStaleElements(()->{
            stringResult = getBrowser().findElement(identifier).getAttribute(attribute);
        });
        return stringResult;
    } 
    
    protected String getCssValue(By identifier, String css) {
        StormLog.info("getting css value '"+css+"' for '" + identifier.toString() + "'", getClass());
        stringResult = "";
        catchStaleElements(()->{
            stringResult = getBrowser().findElement(identifier).getCssValue(css);
        });
        return stringResult;
    }
    
    protected Point getLocation(By identifier) {
        StormLog.info("getting location for " + identifier.toString(), getClass());
        pointResult = null;
        catchStaleElements(()->{
            pointResult = getBrowser().findElement(identifier).getLocation();
        });
        return pointResult;
    }
    
    protected Rectangle getRect(By identifier) {
        StormLog.info("getting rectangle for " + identifier.toString(), getClass());
        rectangleResult = null;
        catchStaleElements(()->{
            rectangleResult = getBrowser().findElement(identifier).getRect();
        });
        return rectangleResult;
    }

    protected Dimension getSize(By identifier) {
        StormLog.info("getting size for " + identifier.toString(), getClass());
        dimensionResult = null;
        catchStaleElements(()->{
            dimensionResult = getBrowser().findElement(identifier).getSize();
        });
        return dimensionResult;
    }
    
    protected String getTagName(By identifier) {
        StormLog.info("getting tag name for " + identifier.toString(), getClass());
        stringResult = "";
        catchStaleElements(()->{
            stringResult = getBrowser().findElement(identifier).getTagName();
        });
        return stringResult;
    }

    protected String getText(By identifier) {
        StormLog.info("getting text for " + identifier.toString(), getClass());
        stringResult = "";
        catchStaleElements(()->{
            stringResult = getBrowser().findElement(identifier).getText();
        });
        return stringResult;
    }

    protected boolean isDisplayed(By identifier) {
        StormLog.info("getting is displayed for " + identifier.toString(), getClass());
        boolResult = false;
        catchStaleElements(()->{
            boolResult = getBrowser().findElement(identifier).isDisplayed();    
        });
        return boolResult;
    }

    protected boolean isEnabled(By identifier) {
        StormLog.info("getting is enabled for " + identifier.toString(), getClass());
        boolResult = false;
        catchStaleElements(()->{
            boolResult = getBrowser().findElement(identifier).isEnabled(); 
        });
        return boolResult;
    }

    protected boolean isSelected(By identifier) {
        StormLog.info("getting is selected for " + identifier.toString(), getClass());
        boolResult = false;
        catchStaleElements(()->{
            boolResult = getBrowser().findElement(identifier).isSelected();  
        });
        return boolResult;
    }
    
    protected void submit(By identifier) {
         StormLog.info("submitting: " + identifier.toString(), getClass());
         catchStaleElements(()->{
            getBrowser().findElement(identifier).submit();  
         });
    }

    protected boolean isElementOnPage(By identifier) {
        StormLog.info("seeing if element exists on the page " + identifier.toString(), getClass());
        boolResult = false;
        catchStaleElements(()->{
            boolResult = getBrowser().findElements(identifier).size() > 0;
        });
        return boolResult;
    }

    protected boolean waitForElementOnPage(By identifier) {
        StormLog.info("waiting for element to exist on the page " + identifier.toString(), getClass());
        boolResult = false;
        catchStaleElements(()->{
            boolResult = StormUtils.waitForTrue(()->getBrowser().findElements(identifier).size() > 0);
        });
        return boolResult;
    }

    protected void doubleClick(WebElement element) {
        StormLog.info("double click " + element.getTagName(), getClass());
        new Actions(getBrowser()).doubleClick(element).build().perform();
    }

    protected void dragAndDrop(WebElement startElement, WebElement endElement) {
        StormLog.info("drag and dropping from " + startElement.getTagName() + " to " + endElement.getTagName(), getClass());
        new Actions(getBrowser()).dragAndDrop(startElement, endElement).build().perform();
    }

    protected void clear(WebElement element) {
        StormLog.info("clearing field " + element.getTagName(), getClass());
        element.clear();
    }
    
    protected String getAttribute(WebElement element, String attribute) {
        StormLog.info("getting attribute '"+attribute+"' from '"+element.getTagName()+"'", getClass());
        return element.getAttribute(attribute);
    } 
    
    protected String getCssValue(WebElement element, String css) {
        StormLog.info("getting css value '"+css+"' for '" + element.getTagName() + "'", getClass());
        return element.getCssValue(css);
    }
    
    protected Point getLocation(WebElement element) {
        StormLog.info("getting location for " + element.getTagName(), getClass());
        return element.getLocation();
    }
    
    protected Rectangle getRect(WebElement element) {
        StormLog.info("getting rectangle for " + element.getTagName(), getClass());
        return element.getRect();
    }

    protected Dimension getSize(WebElement element) {
        StormLog.info("getting size for " + element.getTagName(), getClass());
        return element.getSize();
    }
    
    protected String getTagName(WebElement element) {
        StormLog.info("getting tag name for " + element.getTagName(), getClass());
        return element.getTagName();
    }

    protected String getText(WebElement element) {
        StormLog.info("getting text for " + element.getTagName(), getClass());
        return element.getText();
    }

    protected boolean isDisplayed(WebElement element) {
        StormLog.info("getting is displayed for " + element.getTagName(), getClass());
        return element.isDisplayed();
    }

    protected boolean isEnabled(WebElement element) {
        StormLog.info("getting is enabled for " + element.getTagName(), getClass());
        return element.isEnabled();
    }

    protected boolean isSelected(WebElement element) {
        StormLog.info("getting is selected for " + element.getTagName(), getClass());
        return element.isSelected();
    }
    
    protected void submit(WebElement element) {
         StormLog.info("submitting: " + element.getTagName(), getClass());
         element.submit();
    }
    
    protected void close() {
        StormLog.info("closing the browser", getClass());
        if (SeleniumBuilder.isBrowserInitialzied()) {
            getBrowser().close();   
        }
    }

    protected void quit() {
        StormLog.info("quitting out of the browser", getClass());
        if (SeleniumBuilder.isBrowserInitialzied()) {
            getBrowser().quit();
        }
    }
    
    protected void setWindowfullscreen() {
        StormLog.info("setting window to full screen", getClass());
        getBrowser().manage().window().fullscreen();
    }

    protected void setWindowSize(int width, int height) {
        StormLog.info("setting window size to width: " + width + " height: " + height, getClass());
        getBrowser().manage().window().setSize(new Dimension(width, height));
    }

    protected void setWindowMaximize() {
        StormLog.info("setting window to maximize", getClass());
        getBrowser().manage().window().maximize();
    }

    protected void deleteAllCookies() {
        StormLog.info("deleting all the cookies", getClass());
        if (SeleniumBuilder.isBrowserInitialzied()) {
            getBrowser().manage().deleteAllCookies();
        }
    }

    protected void deleteCookieNamed(String cookieName) {
        StormLog.info("deleting cookie named " + cookieName, getClass());
        if (SeleniumBuilder.isBrowserInitialzied()) {
            getBrowser().manage().deleteCookieNamed(cookieName);
        }
    }

    protected void addCookie(Cookie cookie) {
        StormLog.info("adding cookie named " + cookie.getName(), getClass());
        getBrowser().manage().addCookie(cookie);
    }

    protected void addCookie(String name, String value) {
        StormLog.info("adding cookie, name: " + name + " value: " + value, getClass());
        getBrowser().manage().addCookie(new Cookie(name, value));
    }
    
    protected Set<Cookie> getCookies() {
        StormLog.info("getting all cookies", getClass());
        return getBrowser().manage().getCookies();
    }
    
    protected Cookie getCookieNamed(String name) {
        StormLog.info("getting cookie named " + name, getClass());
        return getBrowser().manage().getCookieNamed(name);
    }

    protected void setImplicitWait(long time, TimeUnit unit) {
        StormLog.info("setting implicit wait time to " + time + " " + unit.name(), getClass());
        getBrowser().manage().timeouts().implicitlyWait(time, unit);
    }

    protected void setPageLoadTimeout(long time, TimeUnit unit) {
        StormLog.info("setting page load timeout to " + time + " " + unit.name(), getClass());
        getBrowser().manage().timeouts().pageLoadTimeout(time, unit);
    }

    protected void setScriptTimeout(long time, TimeUnit unit) {
        StormLog.info("setting script timeout to " + time + " " + unit.name(), getClass());
        getBrowser().manage().timeouts().setScriptTimeout(time, unit);
    }

    protected void setAttribute(By identifier, String attribute, String value) {
        StormLog.info("setting attribute '"+attribute+"' to '"+value+"' for element '"+identifier.toString()+"'", getClass());
        catchStaleElements(()->{
            WebElement element = getBrowser().findElement(identifier);
            ((JavascriptExecutor)getBrowser()).executeScript("arguments[0].setAttribute('"+attribute+"', '"+value+"')", element);
        });
    }
    
    protected void setAttribute(WebElement element, String attribute, String value) {
        StormLog.info("setting attribute '"+attribute+"' to '"+value+"' for element '"+element.getTagName()+"'", getClass());
        ((JavascriptExecutor)getBrowser()).executeScript("arguments[0].setAttribute('"+attribute+"', '"+value+"')", element);
    }

    protected String executeJavaScript(String javascript) {
        StormLog.info("running script: " + javascript, getClass());
        return (String)((JavascriptExecutor)getBrowser()).executeScript(javascript);
    }

    protected void setInnerHtml(By identifier, String innerHtml) {
        StormLog.info("setting innerHML for '"+identifier.toString()+"' to '"+innerHtml+"''", getClass());
        catchStaleElements(()->{
            WebElement element = getBrowser().findElement(identifier);
            ((JavascriptExecutor)getBrowser()).executeScript("arguments[0].innerHTML='"+innerHtml.replace("'", "\\'")+"'", element);
        });

    }
    
    protected void setInnerHtml(WebElement element, String innerHtml) {
        StormLog.info("setting innerHML for '"+element.getTagName()+"' to '"+innerHtml+"''", getClass());
        ((JavascriptExecutor)getBrowser()).executeScript("arguments[0].innerHTML='"+innerHtml.replace("'", "\\'")+"'", element);
    }

    //================================== API calls through the getBrowser() ==================================
    protected ApiResponse post(String path, HashMap<String, String> headers, String body) {
        return apiCall("POST", path, headers, body);
    }

    protected ApiResponse put(String path, HashMap<String, String> headers, String body) {
        return apiCall("PUT", path, headers, body);
    }

    protected ApiResponse patch(String path, HashMap<String, String> headers, String body) {
        return apiCall("PATCH", path, headers, body);
    }

    protected ApiResponse get(String path, HashMap<String, String> headers) {
        return apiCall("GET", path, headers);
    }

    protected ApiResponse delete(String path, HashMap<String, String> headers) {
        return apiCall("DELETE", path, headers);
    }

    protected ApiResponse post(String path, String body) {
        return apiCall("POST", path, new HashMap<>(), body);
    }

    protected ApiResponse put(String path, String body) {
        return apiCall("PUT", path, new HashMap<>(), body);
    }

    protected ApiResponse patch(String path, String body) {
        return apiCall("PATCH", path, new HashMap<>(), body);
    }

    protected ApiResponse get(String path) {
        return apiCall("GET", path, new HashMap<>());
    }

    protected ApiResponse delete(String path) {
        return apiCall("DELETE", path, new HashMap<>());
    }

    protected ApiResponse apiCall(String type, String path, HashMap<String, String> headers, String body) {
        waitForPageLoad();
        
        StringBuilder javaScriptCall = new StringBuilder("window.testresponse=undefined;window.testresponsecode=undefined;\nTAEapicall = new XMLHttpRequest();\n");
        javaScriptCall.append("TAEapicall.open('").append(type.toUpperCase()).append("', '").append(path).append("');\n");
        headers.forEach((k,v)-> javaScriptCall.append("TAEapicall.setRequestHeader('").append(k).append("', '").append(v).append("');\n"));
        javaScriptCall.append("TAEapicall.onload = function() {window.testresponse = TAEapicall.response; window.testresponsecode=TAEapicall.status;};\n");
        javaScriptCall.append("TAEapicall.send(JSON.stringify(").append(body).append("));\n");

        StormLog.info("running api call "+type+" to " + path, getClass());
        StormLog.debug("api call: " + javaScriptCall.toString(), getClass());

        String response = null;
        ((JavascriptExecutor)getBrowser()).executeScript(javaScriptCall.toString());
        int tried = apiTryCount;
        int responseCode = 0;
        Object rawresponse = null;
        while ((rawresponse == null || responseCode == 0) && --tried > 0) {
            StormUtils.sleep(300);
            rawresponse = ((JavascriptExecutor)getBrowser()).executeScript("return window.testresponsecode");
            if (rawresponse != null) {
                responseCode = Math.toIntExact((Long) rawresponse);
            }
        } if (responseCode == 0) {
            StormLog.error("never got a response back for the api call " + path, getClass());
        } else {
            response = (String) ((JavascriptExecutor) getBrowser()).executeScript("return window.testresponse");
            StormLog.info("api call finished with response code " + responseCode, getClass());
        }

        StormLog.debug("api response: " + response, getClass());
        return ApiResponse.create(responseCode, response);
    }

    protected ApiResponse apiCall(String type, String path, HashMap<String, String> headers) {
        waitForPageLoad();

        StringBuilder javaScriptCall = new StringBuilder("window.testresponse=undefined;window.testresponsecode=0;\nTAEapicall = new XMLHttpRequest();\n");
        javaScriptCall.append("TAEapicall.open('").append(type.toUpperCase()).append("', '").append(path).append("');\n");
        headers.forEach((k,v)-> javaScriptCall.append("TAEapicall.setRequestHeader('").append(k).append("', '").append(v).append("');\n"));
        javaScriptCall.append("TAEapicall.onload = function() {window.testresponse = TAEapicall.response; window.testresponsecode=TAEapicall.status;};\n");
        javaScriptCall.append("TAEapicall.send();\n");

        StormLog.info("running api call "+type+" to " + path, getClass());
        StormLog.debug("api call: " + javaScriptCall.toString(), getClass());

        String response = null;
        ((JavascriptExecutor)getBrowser()).executeScript(javaScriptCall.toString());
        int tried = apiTryCount;
        int responseCode = 0;
        Object rawresponse = null;
        while ((rawresponse == null || responseCode == 0) && --tried > 0) {
            StormUtils.sleep(300);
            rawresponse = ((JavascriptExecutor)getBrowser()).executeScript("return window.testresponsecode");
            if (rawresponse != null) {
                responseCode = Math.toIntExact((Long) rawresponse);
            }
        } if (responseCode == 0) {
            StormLog.error("never got a response back for the api call " + path, getClass());
        } else {
            response = (String) ((JavascriptExecutor) getBrowser()).executeScript("return window.testresponse");
            StormLog.info("api call finished with response code " + responseCode, getClass());
        }

        StormLog.debug("api response: " + response, getClass());
        return ApiResponse.create(responseCode, response);
    }

    protected void setApiCallTimeout(int seconds) {
        apiTryCount = seconds * 10;
    }
    //==================================================================================================
}
