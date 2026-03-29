package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert; // Added this
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

    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    /**
     * ✅ Extent-Integrated Soft Assertion
     */
    public void softAssertVisible(WebElement element, String elementName) {
        try {
            boolean isVisible = wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();

            // 1. Log to TestNG (for the final verdict)
            softAssert.get().assertTrue(isVisible, "❌ Element NOT found: " + elementName);

            // 2. Log to Extent Report (for real-time visibility)
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
     * 🏁 Final Validation: MUST be called at the end of the test to report failures.
     */
    public void assertAll() {
        try {
            softAssert.get().assertAll();
        } finally {
            // Clear the collector so the next test starts with a clean slate
            softAssert.remove();
        }
    }

    // --- YOUR EXISTING METHODS ---

    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            System.out.println("Standard click failed. Falling back to JS Click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void sendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        element.sendKeys(text);
    }

    public void typeDate(WebElement element, String dateFromExcel) {
        try {
            // 1. Standard approach: Wait and try to type
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

            // Clear existing value
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
           // element.sendKeys(Keys.BACK_SPACE);

            // Type the new date
            element.sendKeys(dateFromExcel);
            element.sendKeys(Keys.ENTER);

            ExtentManager.getTest().log(Status.INFO, "Date entered via Keyboard: " + dateFromExcel);
        } catch (Exception e) {
            // 2. JS Fallback: If typing fails or the element is intercepted
            ExtentManager.getTest().log(Status.WARNING, "Keyboard entry failed for date. Switching to JS Force...");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('value', '" + dateFromExcel + "')", element);

            // Manually trigger a 'change' event so the site knows the date was updated
            js.executeScript("arguments[0].dispatchEvent(new Event('change'))", element);

            ExtentManager.getTest().log(Status.INFO, "Date entered via JavaScript: " + dateFromExcel);
        }
    }

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

    protected void uploadFile(WebElement element, String filePath) {
        try {

            File file = new File(filePath);
            String absolutePath = file.getAbsolutePath();

            element.sendKeys(absolutePath);
            ExtentManager.getTest().log(Status.INFO, "File uploaded: " + absolutePath);


        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "File upload failed: " + filePath);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    protected void selectReactDropdown(WebElement element, String value) {
        try {
            // 1. Click to open the dropdown
            click(element);

            // 2. Find the input inside the dropdown and type the value
            WebElement input = element.findElement(By.tagName("input"));
            input.sendKeys(value);

            // 3. Wait for the option to appear in the list and click it
            WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'option') and text()='" + value + "']")));
            option.click();

            ExtentManager.getTest().log(Status.INFO, "Selected from dropdown: " + value);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Dropdown selection failed: " + value);
            throw new RuntimeException("Dropdown selection failed: " + e.getMessage());
        }
    }
    protected void selectCheckboxes(List<WebElement> options, String valuesFromExcel) {
        String[] values = valuesFromExcel.split(",");

        for (WebElement option : options) {
            for (String value : values) {
                // Try matching by label text first, then fall back to value attribute
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

    protected void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}