package pageObject.webPage;

import org.openqa.selenium.By;

public class ExamplePage extends WebPage {

    protected final By searchField = By.className("search-field");
    protected final By searchButton = By.className("search-submit");
    protected final By signUpLink = By.linkText("Sign Up!");

    //singleton constructor
    public static ExamplePage getPage() {
        return initialize(ExamplePage.class);
    }

    public void clickSignUpLink() {
        waitForElementOnPage(signUpLink);
        click(signUpLink);
    }

    public void waitForPageLoad() {
        waitForElementOnPage(searchField);
    }

    public void setSearchField(String text) {
        sendKeys(searchField, text);
    }

    public void clickSearchButton() {
        click(searchButton);
    }
}
