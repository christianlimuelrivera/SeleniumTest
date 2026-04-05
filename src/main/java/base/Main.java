package base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import utils.ConfigReader;
import utils.ExtentManager;
import utils.SuiteListener;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Main (Base Test) Class
 * Serves as the foundational class for all test execution.
 * Handles WebDriver initialization, cross-browser configuration,
 * thread safety for parallel execution, and Extent Report logging.
 */
// Registers SuiteListener for ALL subclasses automatically (Report Finalizer).
// Works whether you run from the IDE, testng.xml, or mvn test.
@Listeners(SuiteListener.class)
public class Main {

    // ThreadLocal ensures each parallel thread gets its own isolated WebDriver instance, preventing collisions.
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Retrieves the WebDriver instance for the currently active thread.
     */
    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setup(Method method, Object[] testData) {

        // ============================================================
        // 1. DYNAMIC TEST NAMING FOR REPORTING
        // ============================================================
        String testName = method.getName();

        // If using a DataProvider, append the 'FirstName' (or DataRow) to make the report distinct
        if (testData.length > 0 && testData[0] instanceof Map) {
            Map<String, String> map = (Map<String, String>) testData[0];
            testName += " - " + map.getOrDefault("FirstName", "DataRow");
        }

        // Initialize ExtentTest and bind it to the current thread
        ExtentTest currentTest = ExtentManager.getInstance().createTest(testName);
        ExtentManager.setTest(currentTest);

        // ============================================================
        // 2. ENVIRONMENT & BROWSER CONFIGURATION
        // ============================================================
        // Check system properties first (for CI/CD like Jenkins), fallback to config.properties
        String browser = System.getProperty("browser");
        if (browser == null) {
            browser = ConfigReader.getProperty("browser");
        }
        browser = browser.toLowerCase();

        String headless = ConfigReader.getProperty("headless");
        boolean isHeadless = headless != null && headless.equalsIgnoreCase("true");

        ExtentManager.getTest().log(Status.INFO, "Starting thread: " + Thread.currentThread().getId()
                + " | Browser: " + browser + " | Headless: " + isHeadless);

        // ============================================================
        // 3. WEBDRIVER INITIALIZATION (Using Native Selenium Manager)
        // ============================================================
        WebDriver localDriver;
        switch (browser) {
            case "firefox":
                // Removed WebDriverManager - Selenium v4.6+ handles this natively!
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (isHeadless) ffOptions.addArguments("-headless");
                localDriver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                EdgeOptions eOptions = new EdgeOptions();
                if (isHeadless) eOptions.addArguments("--headless=new");
                localDriver = new EdgeDriver(eOptions);
                break;

            default: // Chrome acts as the default fallback
                ChromeOptions cOptions = new ChromeOptions();
                if (isHeadless) {
                    cOptions.addArguments("--headless=new");
                    cOptions.addArguments("--disable-gpu");
                    cOptions.addArguments("--window-size=1920,1080");
                }
                localDriver = new ChromeDriver(cOptions);
                break;
        }

        // Attach the driver to the current thread
        driver.set(localDriver);

        // Navigate and configure window
        getDriver().get(ConfigReader.getProperty("url"));
        getDriver().manage().window().maximize();
    }

    @AfterMethod
    public void teardown(ITestResult result) {

        // ============================================================
        // 4. TEARDOWN & FAILURE SCREENSHOTS
        // ============================================================
        if (getDriver() != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                // Capture Base64 screenshot to embed directly in the HTML report (No broken image links)
                String base64 = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
                ExtentManager.getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());
                ExtentManager.getTest().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                ExtentManager.getTest().log(Status.PASS, "Test execution successful.");
            }

            // Safely close the browser
            getDriver().quit();
        }

        // ============================================================
        // 5. THREAD CLEANUP
        // ============================================================
        // Crucial: removes the driver and test from memory to prevent leaks in subsequent tests
        driver.remove();
        ExtentManager.removeTest();
    }
}