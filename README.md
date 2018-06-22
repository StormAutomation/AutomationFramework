# Automation Framework #
[StormAutomation.com](http://StormAutomation.com)

Selenium framework using the page object model for you to start writing your automated tests with

### Setup ###

clone this repository to your local computer, and using an IDE choose import project
choose the directory that the repo was cloned into as the project to import
select the project type as maven
continue to go through the setup leaving the default settings as they are
make sure it is set to use java 1.8 SDK

### Run A selenium test ###

Tests are kept in the directory src/test/java
in this directory you will find a package called 'sample' and in that package the java class 'SeleniumTest'
if you open this class you will see the sample test 'myFirstTest()' notice this has @Test above it
the framework uses the TestNG testing framework and the @Test is how TestNG knows this method is a test

first build the whole project, if this results in an error, try waiting another minute or so
the IDE will be importing all the settings for the maven project and will fail if it has not finished
once you have the project built you can right click on the test and find the option 'Run myFirstTest()' or 'Run Test'
the test will start, and default is set to use chrome. 
- to use other browsers like firefox or IE you need to make sure they are installed on your computer

### Creating Your First Test - Creating Page Objects ###
This framework follows the Page Object model - meaning that for every page of the site you are testing you should have a corresponding java class associated to it.
these classes should be added under the directory src/main/java/pageObject/ you will see an example one titled 'ExamplePage' to give an example of what the page objects class should look like

There is also a file there called 'PageConfiguration' this file is a page object that is for setting up the browser, things like navigation and maximize window

The other file, WebPage is used for inheritance in the page objects, notice in the page object examples the line `extends WebPage`

Each page object class needs to be a new file in src/main/java/pageObject/. A standard Page Object should look like this:
(example of a login page)
```
package pageObject;

import org.openqa.selenium.By;
import webDriverBuilder.SeleniumBuilder;

public class LoginPage extends WebPage {

    //By objects used by selenium to find elements on a webpage
    protected final By username = By.id("username");
    protected final By password = By.id("password");
    protected final By loginBtn = By.id("login");

    //singleton constructor
    public static LoginPage getPage() {
        return initialize(LoginPage.class);
    }

    //declare methods that can interact with the page
    //these methods can be as big and complex or as small and simple as you like
    // *small and simple is usually either to maintain

    public void login() {
    	//inside the method is where you write selenium code that interacts with the page
    	//to see all the actions you can do see src/main/java/pageObject/WebPage.java 
    	//anything set to 'protected' can be accessed here
    	
    	sendKeys(username "myusername");
    	sendKeys(password "mypassword");
    	click(loginBtn);
    }
}
```

### Creating Your First Test -  Writing The Test ###
To organize your tests you should have packages or directories for different types of tests, for this example we could create a directory called 'login' and then in that directory we could create a file 'ValidLoginTest.java' this would make the full path 'src/test/java/login/ValidLoginTest.java'

To create a test method we need to create a normal method, naming the method what we want the name of the test to be. 
Then above the method add `@Test`, this is an annotation that notifies TestNG that this is a test method

In our test to use the page objects we have created we will call the class by the name we gave it, in this example that would be 'LoginPage' then we call 'getPage()' on that class, this is what will give us access to the methods we created in that class, so the call to use the login method would be `LoginPage.getPage().login();`

To make it a test we need to add asserts. TestNG has built in assert methods, the standard for these asserts is the first parameter is our actual and the second parameter is the expected, the third parameter is optional and is a message that is displayed if the assert fails. let's say we wanted to assert the URL showed that we were logged in that would look like this: 
`Assert.assertEquals(PageConfiguration.getPage().getCurrentUrl(), "http://www.myApp.com/loggedIn", "the url does not show that you were logged in");`

in this assert we are using assertEquals, first we give it our actual url by getting it from our PageConfiguration object, by doing `PageConfiguration.getPage().getCurrentUrl()` this will give us back the current url of the page. then we gave it what we are expecting it to be `"http://myApp.com/loggedIn"`, then last we gave it the optional parameter of a message to be shown if this assert fails `"the url does not show that you were logged in"`

bringing the whole test together:
```
package login;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObject.LoginPage;
import pageObject.PageConfiguration;


public class ValidLoginTest {

    @Test
    public void myFirstTest() {
        PageConfiguration.getPage().navigateTo("http://www.myApp.com");
        LoginPage.getPage().login();
        Assert.assertEquals(PageConfiguration.getPage().getCurrentUrl(), "http://www.myApp.com/loggedIn", "the url does not show that you were logged in");
    }
}
```

you can include as many test methods as you would like in a single class, and you can include as many classes as you would like in a single package, but do not be afraid to make more packages, including packages inside packages. Use whatever is best to organize your given tests

### API Testing ###

there is 2 ways to do api testing
1. a standard api call that will execute through the frameworks api client src/main/java/utilities/api/Call
2. execute the api call through the browser using javascript command

using the first way for standard api testing that will not open the browser would look like this: `ApiResponse apiResponse = Call.post("https://httpbin.org/post", "{\"StormCall\":\"Pass\"}");`

using the second way through the browser (this way allows you to make calls like the website would and use any cookies that may be set in the browser): `ApiResponse apiResponse = PageConfiguration.getPage().post("https://httpbin.org/post", "{\"StormCall\":\"Pass\"}");`

both ways of calling API's use the ApiResponse object which can be found at src/main/java/utilities/api/ApiResponse, this object has the following methods: 

* int getResposeCode() 
* String getResponse() 
* JSONObject getJSON() 
* JSONArray getJSONArray() 
* <T> T getObject(Class mapTo)

the getObject method will be if you have an object that you want the json response mapped to. so if you have a class 'MyResponse' that maps to the response you get back from the api call, then the code would look like this `MyResponse myResponse = apiResponse.getObject(MyResponse.class);`

### Email Testing ###

need to create an email account for testing with gmail. When email is created change these settings:

* enable imap in settings -> https://support.google.com/mail/answer/7126229?hl=en
* allow less secure apps -> https://myaccount.google.com/lesssecureapps?pli=1

add the username and password for the email account in src/main/resources/default.properties
set the property 'realEmaillAddress' to the email address created with google and 'realEmailPassword' to the password for that email.

### Utilities - Email Client ###
need to import the utilities email package
```
import utilities.email;
```

retrieve emails from the email api client. file is located at src/main/java/utilities/email/EmailApi.java. object has the following methods:

* EmailMessage[] EmailApi.emailClient.getEmails(10, 20);
* int EmailApi.emailClient().getEmailCount();
* boolean EmailApi.emailClient().waitForEmail();
* boolean EmailApi.emailClient().waitForEmail("subject");
* EmailMessage EmailApi.emailClient().getEmail(1);
* EmailMessage EmailApi.emailClient().getEmail("subject");
* EmailMessage EmailApi.emailClient().getMostRecentEmail();
* EmailMessage[] EmailApi.emailClient().getRecentEmails();
* EmailMessage[] EmailApi.emailClient().getRecentEmails(15);

### Utilities - Email Message ###

When you get a message from the email client it returns the message as EmailMessage object, file for this is at src/main/java/utilities/email/EmailApi.java

EmailMessage is used to get information from an individual email, EmailMessage has the following methods:

* javax.mail.Message emailMessage.getRawMessage();
* emailMessage.markEmailAsRead();
* emailMessage.markEmailAsUnread();
* emailMessage.deleteEmail();
* String emailMessage.getSubject();
* String emailMessage.getBody();
* List<String> emailMessage.getAttachments();
* String[] emailMessage.getFrom();
* String[] emailMessage.getRecipients();
* String[] emailMessage.getReplyTo();
* Date emailMessage.getSentDate();
* Date emailMessage.getReceivedDate();
* int emailMessage.getMessageNumber();
* boolean emailMessage.bodyContains("text");
* boolean emailMessage.attachmentContains("text");
* boolean emailMessage.hasAttachment();

### utlities - DataGenerator ###
The DataGenerator is for when you need data for your test and you want it to be random. This will generate random data so it will be unique data everytime you call one of the methods. available methods to generated data are: 

* String generateLetters() 
* String generateLetters(int length) 
* String generateString() 
* String generateString(int length) 
* int generateNumber() 
* int generateNumber(int length) 
* int generateNumber(int min, int max) 
* long generateLongNumber(long min, long max) 
* String generateEmail() 
* String generatePhone() 
* DateTime generateDateTimeAfter(DateTime after) 
* DateTime generateDateTimeAfterNow() 
* DateTime generateDateTimeBefore(DateTime before) 
* DateTime generateDateTimeBeforeNow() 
* String generateStreetAddress() 

### utlities - StormLog ###
the logger uses the TestNG logging. and has the standard logging methods 'info', 'debug', 'warn', 'error', each taking parameters of the message to be logged in and the class that the log message is coming from. To configure the logger see the property file at src/main/resources/log.properties


### utlities - StormProperties ###
StormProperties is used to get properties from the property files located at src/main/resources/. You can have properties constructed from other properties by using {{var}} for variables example: 
```
test=My {{property}}
property=Test Var
//when you getProperty('test') will give back "My Test Var"
```
If you want to add a property you can add it to default.properties or to add a new file of properties, add the .properties file in that directory and then add the name of the file in 'default.properties' under the field 'additionalPropertyFiles', comma delimit the list of files.

The property reader will first look for system level variables and then if not found check the property file. Also while writing the tests StormProperties allows to 'setProperty(String key, String value)'

### utlities - StormUtils ###
for any additional utility methods needed. currently this class has a 'sleep(long millisecons)' and a 'waitForTrue(BooleanSupplier evaluate)' - used for waiting for an expression to be true, (default time out at 5 seconds). example how to use it:

```
//wait for the current url to have "loggedIn" at the end of it. will timeout at 5 seconds of waiting
StormUtils.waitForTrue(()->{PageConfiguration.getPage().getCurrentUrl().endsWith("loggedIn");});
```

[StormAutomation.com](http://StormAutomation.com)