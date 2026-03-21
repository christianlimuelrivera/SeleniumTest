package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * 🛡️ BASEPAGE - The Guardian of the Pages
 * This class handles the "Synchronizing" (Waiting) for all Page Objects.
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // ⏱️ Global Explicit Wait: 10 seconds.
        // It won't wait 10 seconds every time; it will move on as soon as the element is ready.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * 🖱️ Smart Click: Waits for element to be clickable before clicking.
     */
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * ⌨️ Smart Type: Waits for element to be visible, clears it, then types.
     */
    protected void sendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        element.sendKeys(text);
    }

    /**
     * 👁️ Visibility Check: Useful for verifying success messages.
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}