package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utils.ConfigReader;
import utils.ExtentManager;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // 🧵 Thread-safe SoftAssert: Keeps error logs separate for each parallel test.
    protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

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
            // First check if element is displayed
            // This acts as an extra wait for animations to complete
            isDisplayed(element);

            // Then wait for visibility
            wait.until(ExpectedConditions.visibilityOf(element));

            String text = element.getText();

            if (text == null || text.isEmpty()) {
                text = element.getAttribute("value");
            }
            if (text == null || text.isEmpty()) {
                text = (String) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].innerText;", element);
            }

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
// ALERT HANDLING
// ============================================================

    /**
     * Waits for an alert to appear and accepts it (clicks OK).
     */
    protected void acceptAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            ExtentManager.getTest().log(Status.INFO, "Alert accepted");
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Alert not found");
            throw new RuntimeException("acceptAlert() failed: " + e.getMessage());
        }
    }

    /**
     * Waits for an alert and dismisses it (clicks Cancel).
     */
    protected void dismissAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
            ExtentManager.getTest().log(Status.INFO, "Alert dismissed");
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Alert not found");
            throw new RuntimeException("dismissAlert() failed: " + e.getMessage());
        }
    }

    /**
     * Gets the text from an alert without closing it.
     */
    protected String getAlertText() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String text = driver.switchTo().alert().getText();
            ExtentManager.getTest().log(Status.INFO, "Alert text: " + text);
            return text;
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not get alert text");
            throw new RuntimeException("getAlertText() failed: " + e.getMessage());
        }
    }

    /**
     * Types text into a prompt alert then accepts it.
     */
    protected void typeInAlert(String text) {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.sendKeys(text);
            alert.accept();
            ExtentManager.getTest().log(Status.INFO, "Typed in alert: " + text);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not type in alert");
            throw new RuntimeException("typeInAlert() failed: " + e.getMessage());
        }
    }

    // ============================================================
// IFRAME HANDLING
// ============================================================

    /**
     * Switches into an iFrame by WebElement.
     * Scrolls into view first to ensure frame is visible.
     */
    protected void switchToFrame(WebElement frameElement) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", frameElement);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
            ExtentManager.getTest().log(Status.INFO, "Switched into iFrame");
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not switch to iFrame");
            throw new RuntimeException("switchToFrame() failed: " + e.getMessage());
        }
    }

    /**
     * Switches into an iFrame by id or name attribute.
     */
    protected void switchToFrameById(String idOrName) {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(idOrName));
            ExtentManager.getTest().log(Status.INFO, "Switched into iFrame: " + idOrName);
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not switch to iFrame: " + idOrName);
            throw new RuntimeException("switchToFrameById() failed: " + e.getMessage());
        }
    }

    /**
     * Switches back to the main page from inside an iFrame.
     */
    protected void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
            ExtentManager.getTest().log(Status.INFO, "Switched back to main page");
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not switch to default content");
            throw new RuntimeException("switchToDefaultContent() failed: " + e.getMessage());
        }
    }

    /**
     * Switches to parent frame from a nested iFrame.
     */
    protected void switchToParentFrame() {
        try {
            driver.switchTo().parentFrame();
            ExtentManager.getTest().log(Status.INFO, "Switched to parent frame");
        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not switch to parent frame");
            throw new RuntimeException("switchToParentFrame() failed: " + e.getMessage());
        }
    }

    /**
     * Reads a row from either a standard HTML table or React table.
     * Automatically detects the table type and handles accordingly.
     * Excludes the last column (Action column).
     *
     * @param searchBox  the search input element
     * @param searchTerm the value to search for (e.g. First Name)
     * @return Map of header → cell value
     */
    protected Map<String, String> getTableRow(WebElement searchBox, String searchTerm) {
        try {
            sendKeys(searchBox, searchTerm);
            pause(1);
            List<WebElement> headers;
            List<WebElement> cells;

            // Auto-detect table type
            boolean isStandardTable = !driver.findElements(
                    By.xpath("//thead//th")).isEmpty();

            if (isStandardTable) {
                // Standard HTML table
                ExtentManager.getTest().log(Status.INFO, "Detected standard HTML table");
                headers = driver.findElements(By.xpath("//thead//th"));
                cells = driver.findElements(
                        By.xpath("//tbody//tr[.//td[text()='" + searchTerm + "']]//td"));
            } else {
                // React table
                ExtentManager.getTest().log(Status.INFO, "Detected React table");
                headers = driver.findElements(
                        By.xpath("//div[contains(@class,'rt-th')]"));
                cells = driver.findElements(
                        By.xpath("//div[@class='rt-tr' and .//div[text()='"
                                + searchTerm + "']]//div[@class='rt-td']"));
            }

            // Map headers to cells dynamically
            Map<String, String> rowData = new HashMap<>();
            for (int i = 0; i < headers.size() - 1; i++) { // -1 excludes Action column
                rowData.put(getText(headers.get(i)), getText(cells.get(i)));
            }

            ExtentManager.getTest().log(Status.INFO,
                    "Retrieved table row for: " + searchTerm);
            return rowData;

        } catch (Exception e) {
            ExtentManager.getTest().log(Status.FAIL, "Could not read table row");
            throw new RuntimeException("getTableRow() failed: " + e.getMessage());
        }
    }

    protected void hoverAndClick(WebElement element) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();
            ExtentManager.getTest().log(Status.INFO, "Hover-clicked element");
        } catch (Exception e) {
            // Fallback to JS click if Actions fails
            ExtentManager.getTest().log(Status.WARNING,
                    "Actions click failed, falling back to JS click");
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", element);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
        }
    }

    public void assertEqualsAndLog(Object actual, Object expected, String message) {
        // 1. Convert everything to String to avoid Integer vs String mismatches
        String actualStr = (actual == null) ? "null" : String.valueOf(actual);
        String expectedStr = (expected == null) ? "null" : String.valueOf(expected);

        try {
            // 2. Perform the actual TestNG assertion
            Assert.assertEquals(actualStr, expectedStr, message);

            // 3. Log Success with a little bit of HTML formatting for the report
            ExtentManager.getTest().log(Status.PASS,
                    "<b>" + message + "</b> validated: " + expectedStr);

        } catch (AssertionError e) {
            // 4. Log Failure with clear details
            ExtentManager.getTest().log(Status.FAIL,
                    "<b>" + message + "</b> FAILED! <br>Expected: [" + expectedStr + "] <br>Actual: [" + actualStr + "]");

            throw e; // Critical: still fails the TestNG test
        }
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