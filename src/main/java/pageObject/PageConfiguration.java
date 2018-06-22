package pageObject;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import pageObject.webPage.WebPage;
import utilities.StormProperties;
import utilities.api.ApiResponse;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PageConfiguration extends WebPage {

    //singleton constructor
    public static PageConfiguration getPage() {
        return initialize(PageConfiguration.class);
    }

    @Override
    public void navigateTo(String url) {
        super.navigateTo(url);
    }

    @Override
    public void navigateBack() {
        super.navigateBack();
    }

    @Override
    public void navigateForward() {
        super.navigateForward();
    }

    @Override
    public void refreshPage() {
        super.refreshPage();
    }

    @Override
    public String getCurrentUrl() {
        return super.getCurrentUrl();
    }

    @Override
    public boolean waitForPageUrlToContain(String url) {
        return super.waitForPageUrlToContain(url);
    }

    @Override
    public boolean waitForPageUrlToEndWith(String url) {
        return super.waitForPageUrlToEndWith(url);
    }

    @Override
    public boolean waitForPageUrlToStartWith(String url) {
        return super.waitForPageUrlToStartWith(url);
    }
    
    @Override
    public void close() {
        super.close();
    }

    @Override
    public void quit() {
        super.quit();
    }

    @Override
    public String getWindowHandle() {
        return super.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        return super.getWindowHandles();
    }

    @Override
    public void switchToWindow(String windowHandle) {
        super.switchToWindow(windowHandle);
    }

    @Override
    public Alert switchToAlert() {
        return super.switchToAlert();
    }

    @Override
    public void switchToFrame(WebElement frame) {
        super.switchToFrame(frame);
    }

    @Override
    public void switchToFrame(int frame) {
        super.switchToFrame(frame);
    }

    @Override
    public void switchToFrame(String frame) {
        super.switchToFrame(frame);
    }

    @Override
    public WebElement switchToActiveElement() {
        return super.switchToActiveElement();
    }

    @Override
    public void switchToDefaultContent() {
        super.switchToDefaultContent();
    }

    @Override
    public void switchToParentFrame() {
        super.switchToParentFrame();
    }

    @Override
    public void switchToIframe() {
        super.switchToIframe();
    }

    @Override
    public void switchToTab(int index) {
        super.switchToTab(index);
    }

    @Override
    public void setWindowfullscreen() {
        super.setWindowfullscreen();
    }

    @Override
    public void setWindowSize(int width, int height) {
        super.setWindowSize(width, height);
    }

    @Override
    public void setWindowMaximize() {
        super.setWindowMaximize();
    }

    @Override
    public void deleteAllCookies() {
        super.deleteAllCookies();
    }

    @Override
    public void deleteCookieNamed(String cookieName) {
        super.deleteCookieNamed(cookieName);
    }

    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
    }

    @Override
    public void addCookie(String name, String value) {
        super.addCookie(name, value);
    }

    @Override
    public Set<Cookie> getCookies() {
        return super.getCookies();
    }
    
    @Override
    public Cookie getCookieNamed(String name) {
        return super.getCookieNamed(name);
    }

    @Override
    public void setImplicitWait(long time, TimeUnit unit) {
        super.setImplicitWait(time, unit);
    }

    @Override
    public void setPageLoadTimeout(long time, TimeUnit unit) {
        super.setPageLoadTimeout(time, unit);
    }

    @Override
    public void setScriptTimeout(long time, TimeUnit unit) {
        super.setScriptTimeout(time, unit);
    }

    @Override
    public ApiResponse post(String path, HashMap<String, String> headers, String body) {
        return super.post(path, headers, body);
    }

    @Override
    public ApiResponse put(String path, HashMap<String, String> headers, String body) {
        return super.put(path, headers, body);
    }

    @Override
    public ApiResponse patch(String path, HashMap<String, String> headers, String body) {
        return super.patch(path, headers, body);
    }

    @Override
    public ApiResponse get(String path, HashMap<String, String> headers) {
        return super.get(path, headers);
    }

    @Override
    public ApiResponse delete(String path, HashMap<String, String> headers) {
        return super.delete(path, headers);
    }

    @Override
    public ApiResponse post(String path, String body) {
        return super.post(path, body);
    }

    @Override
    public ApiResponse put(String path, String body) {
        return super.put(path, body);
    }

    @Override
    public ApiResponse patch(String path, String body) {
        return super.patch(path, body);
    }

    @Override
    public ApiResponse get(String path) {
        return super.get(path);
    }

    @Override
    public ApiResponse delete(String path) {
        return super.delete(path);
    }

}
