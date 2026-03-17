package base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.ExtentManager;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 🏗️ MAIN CLASS - The Foundation of the Framework
 * This class handles the Browser setup (BeforeMethod) and Cleanup (AfterMethod).
 * Every Test class you write should "extend Main" to inherit these capabilities.
 */
public class Main {

    // 🧵 ThreadLocal ensures "Thread Safety."
    // If you run 5 tests at once, each test gets its own isolated browser instance
    // so they don't crash into each other.
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // 🏎️ Helper method to grab the correct driver for the current running thread.
    public WebDriver getDriver() {
        return driver.get();
    }

    /**
     * 🏁 SETUP METHOD
     * Runs automatically BEFORE every @Test method.
     */
    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setup(Method method, Object[] testData) {

        // 1️⃣ IDENTIFY TEST NAME:
        // Grabs the name of the test from the code. If using Excel data,
        // it appends the "UserName" so your report shows exactly which row failed.
        String testName = method.getName();
        if (testData.length > 0 && testData[0] instanceof Map) {
            Map<String, String> map = (Map<String, String>) testData[0];
            testName += " - " + map.getOrDefault("UserName", "DataRow");
        }

        // 2️⃣ EXTENT REPORT INITIALIZATION:
        // Creates a new entry in your HTML report for this specific test.
        ExtentTest currentTest = ExtentManager.getInstance().createTest(testName);
        ExtentManager.setTest(currentTest);

        // 3️⃣ BROWSER SELECTION LOGIC:
        // Priority 1: Check Maven command line (mvn test -Dbrowser=firefox)
        // Priority 2: If no Maven flag, check config.properties file
        String browser = System.getProperty("browser");
        if (browser == null) {
            browser = ConfigReader.getProperty("browser");
        }
        browser = browser.toLowerCase();

        // Log which thread and browser are starting in the report
        ExtentManager.getTest().log(Status.INFO, "Starting thread: " + Thread.currentThread().getId() + " | Browser: " + browser);

        // 4️⃣ WEBDRIVER INITIALIZATION:
        // WebDriverManager automatically downloads the correct driver .exe file for you.
        WebDriver localDriver;
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                localDriver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                localDriver = new EdgeDriver();
                break;
            default: // Defaults to Chrome
                WebDriverManager.chromedriver().setup();
                localDriver = new ChromeDriver();
                break;
        }

        // 5️⃣ STORE DRIVER & NAVIGATE:
        // Save the driver into the ThreadLocal "slot" and open the URL from config.properties.
        driver.set(localDriver);
        getDriver().get(ConfigReader.getProperty("url"));
        getDriver().manage().window().maximize();
    }

    /**
     * 🧹 TEARDOWN METHOD
     * Runs automatically AFTER every @Test method.
     */
    @AfterMethod
    public void teardown(ITestResult result) {
        if (getDriver() != null) {

            // 📸 FAILURE HANDLING:
            // If the test failed, take a screenshot, convert to Base64, and attach to the report.
            if (result.getStatus() == ITestResult.FAILURE) {
                String base64 = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
                ExtentManager.getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());
                ExtentManager.getTest().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
            }
            // ✅ SUCCESS HANDLING:
            else if (result.getStatus() == ITestResult.SUCCESS) {
                ExtentManager.getTest().log(Status.PASS, "Test execution successful.");
            }

            // 🛑 CLOSE BROWSER:
            getDriver().quit();
        }

        // 🧽 MEMORY CLEANUP:
        // Remove the driver and test info from the thread to keep memory clean.
        driver.remove();
        ExtentManager.removeTest();
        ExtentManager.getInstance().flush(); // Writes all results into the final HTML file
    }
}