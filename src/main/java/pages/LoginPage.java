package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// 🔗 "extends BasePage" gives this class access to the 'click' and 'wait' methods
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        // 🏗️ 'super(driver)' sends the driver to the BasePage constructor to set up the Wait
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id='username']")
    WebElement username;

    @FindBy(xpath = "//*[@id='password']")
    WebElement password;

    @FindBy(xpath = "//*[@id='submit']")
    WebElement loginBtn;

    public void login(String user, String pass) {
        // ✅ No more raw .click() or .sendKeys()
        // We use the "Smart" versions from BasePage
        sendKeys(username, user);
        sendKeys(password, pass);
        click(loginBtn);
    }
}