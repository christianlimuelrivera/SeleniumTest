package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * 🏠 PAGE OBJECT MODEL (POM) - The LoginPage Expert
 * This class stores all the Locators (XPaths) and Actions (Methods)
 * for the Login screen in one place.
 */
public class LoginPage {

    WebDriver driver;

    /**
     * 🏗️ CONSTRUCTOR: The "Initialization" phase.
     * When you create a 'new LoginPage(driver)', this runs.
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;

        // 🪄 PAGEFACTORY: This is a Selenium "Magic" tool.
        // It initializes all the @FindBy elements listed below.
        // Without this, 'username' and 'password' would be null.
        PageFactory.initElements(driver, this);
    }

    // 🎯 LOCATORS: Using @FindBy is the professional way to find elements.
    // It keeps your code clean and separates "Where the element is" from "What to do with it."

    @FindBy(xpath = "//*[@id='username']")
    WebElement username;

    @FindBy(xpath = "//*[@id='password']")
    WebElement password;

    @FindBy(xpath = "//*[@id='submit']")
    WebElement loginBtn;

    /**
     * 🏃 ACTION METHOD: This is the "Service" the page provides.
     * Instead of writing 3 lines of code in your test, you just call login("user", "pass").
     */
    public void login(String user, String pass) {
        username.sendKeys(user);
        password.sendKeys(pass);
        loginBtn.click();
    }
}