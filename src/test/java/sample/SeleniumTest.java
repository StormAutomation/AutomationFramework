package sample;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pageObject.webPage.ExamplePage;
import pageObject.PageConfiguration;
import utilities.api.ApiResponse;
import utilities.api.Call;


public class SeleniumTest {

    @AfterMethod
    public void closeBrowser() {
        PageConfiguration.getPage().quit();
    }

    @Test
    public void myFirstTest() {
        PageConfiguration.getPage().setWindowMaximize();
        PageConfiguration.getPage().navigateTo("http://stormautomation.com");
        ExamplePage.getPage().clickSignUpLink();
        PageConfiguration.getPage().navigateTo("http://httpbin.org");
        ApiResponse response = PageConfiguration.getPage().get("https://httpbin.org/get");
        Assert.assertEquals(response.getResponseCode(), 200);
        response = Call.get("https://httpbin.org/get");
        Assert.assertEquals(response.getResponseCode(), 200);
    }

}
