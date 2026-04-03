package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import utils.ConfigReader;
import utils.ExtentManager;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // 🧵 Thread-safe SoftAssert: Keeps error logs separate for each parallel test.
    protected static ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

    // ============================================================
    // CONSTRUCTOR
    // Initializes WebDriver and WebDriverWait using timeout from config
    // ============================================================
    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // ============================================================
    // ASSERTION METHODS
    // ============================================================

    /**
     * Soft assertion that checks if an element is visible.
     * Logs result to both TestNG and Extent Report.
     * Does NOT stop the test on failure — collects all failures.
     */
    public void softAssertVisible(WebElement element, String elementName) {
        try {
            boolean isVisible = wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
            softAssert.get().assertTrue(isVisible, "❌ Element NOT found: " + elementName);

            if (isVisible) {
                ExtentManager.getTest().log(Status.PASS, "Verified Visible: " + elementName);
            } else {
                ExtentManager.getTest().log(Status.FAIL, "Verification Failed: " + elementName);
            }
        } catch (Exception e) {
            softAssert.get().fail("❌ Timeout: " + elementName + " did not appear.");
            ExtentManager.getTest().log(Status.FAIL, "Timeout: " + elementName + " was not found on the page.");
        }
    }

    /**
     * Finalizes all soft assertions.
     * MUST be called at the end of every test.
     * Clears the collector so next test starts fresh.
     */
    public void assertAll() {
        try {
            softAssert.get().assertAll();
        } finally {
            softAssert.remove();
        }
    }

    // ============================================================
    // INTERACTION METHODS
    // ============================================================

    /**
     * Clicks an element with built-in wait.
     * Falls back to JavaScript click if standard click fails.
     */
    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            System.out.println("Standard click failed. Falling back to JS Click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Clears and types text into an element with built-in wait.
     */
    protected void sendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        element.sendKeys(text);
    }

    /**
     * Types a date into a date picker.
     * Falls back to JavaScript if keyboard entry fails.
     */
    public void typeDate(WebElement element, String dateFromExcel) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(dateFromExcel);
            element.sendKeys(Keys.ENTER);
            ExtentManager.getTest().log(Status.INFO, "Date entered via Keyboard: " + dateFromExcel);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.WARNING, "Keyboard entry failed. Switching to JS...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('value', '" + dateFromExcel + "')", element);
            js.executeScript("arguments[0].dispatchEvent(new Event('change'))", element);
            ExtentManager.getTest().log(Status.INFO, "Date entered via JavaScript: " + dateFromExcel);
        }
    }

    /**
     * Selects a radio button by matching value attribute or visible text.
     * Stops after first match since only one radio can be selected.
     */
    protected void selectRadioButtonByValue(List<WebElement> options, String valueFromExcel) {
        for (WebElement option : options) {
            if (option.getAttribute("value").equalsIgnoreCase(valueFromExcel) ||
                    option.getText().equalsIgnoreCase(valueFromExcel)) {
                if (!option.isSelected()) {
                    wait.until(ExpectedConditions.elementToBeClickable(option)).click();
                }
                break;
            }
        }
    }

    /**
     * Selects one or more checkboxes by matching visible text or value attribute.
     * Accepts comma-separated values e.g. "Sports,Reading"
     */
    protected void selectCheckboxes(List<WebElement> options, String valuesFromExcel) {
        String[] values = valuesFromExcel.split(",");

        for (WebElement option : options) {
            for (String value : values) {
                boolean matchesText = option.getText().equalsIgnoreCase(value.trim());
                boolean matchesValue = option.getAttribute("value") != null &&
                        option.getAttribute("value").equalsIgnoreCase(value.trim());

                if (matchesText || matchesValue) {
                    click(option);
                }
            }
        }
        ExtentManager.getTest().log(Status.INFO, "Selected checkboxes: " + valuesFromExcel);
    }

    /**
     * Uploads a file by converting relative path to absolute path.
     * Works for any file type — images, PDFs, etc.
     */
    protected void uploadFile(WebElement element, String filePath) {
        try {
            String absolutePath = new File(filePath).getAbsolutePath();
            element.sendKeys(absolutePath);
            ExtentManager.getTest().log(Status.INFO, "File uploaded: " + absolutePath);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "File upload failed: " + filePath);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Selects a value from a React Select dropdown.
     * Clicks to open, types to filter, then clicks the matching option.
     */
    protected void selectReactDropdown(WebElement element, String value) {
        try {
            click(element);
            WebElement input = element.findElement(By.tagName("input"));
            input.sendKeys(value);
            WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'option') and text()='" + value + "']")));
            option.click();
            ExtentManager.getTest().log(Status.INFO, "Selected from dropdown: " + value);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Dropdown selection failed: " + value);
            throw new RuntimeException("Dropdown selection failed: " + e.getMessage());
        }
    }

    // ============================================================
    // TEXT RETRIEVAL
    // ============================================================

    /**
     * Gets visible text from an element with built-in wait.
     * Falls back to value attribute, then JavaScript innerText.
     */
    protected String getText(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            String text = element.getText();

            if (text == null || text.isEmpty()) {
                text = element.getAttribute("value");
            }
            if (text == null || text.isEmpty()) {
                text = (String) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].innerText;", element);
            }

            ExtentManager.getTest().log(Status.INFO, "Got text: " + text);
            return text;
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not get text from element");
            throw new RuntimeException("getText() failed: " + e.getMessage());
        }
    }

    // ============================================================
    // WINDOW / TAB HANDLING
    // ============================================================

    /**
     * Switches to a newly opened tab or window.
     * Waits until more than 1 window handle exists before switching.
     */
    protected void switchToNewTab() {
        // Wait until more than 1 window is open
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        String currentHandle = driver.getWindowHandle();

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        ExtentManager.getTest().log(Status.INFO, "Switched to new tab/window");
    }

    /**
     * Closes the current tab/window and switches back to the original.
     */
    protected void closeCurrentAndSwitchBack(String originalTab) {
        driver.close();
        driver.switchTo().window(originalTab);
        ExtentManager.getTest().log(Status.INFO, "Closed tab/window and switched back");
    }

    /**
     * Switches back to the original tab/window using a saved handle.
     */
    protected void switchToOriginalTab(String originalHandle) {
        driver.switchTo().window(originalHandle);
        ExtentManager.getTest().log(Status.INFO, "Switched back to original tab");
    }

    /**
     * Returns the current window handle.
     * Used to save the original tab before opening a new one.
     */
    protected String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    /**
     * Checks if an element is visible on the page.
     * Returns false instead of throwing an exception if not found.
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Pauses execution for a given number of seconds.
     * Use sparingly — prefer explicit waits where possible.
     */
    protected void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}