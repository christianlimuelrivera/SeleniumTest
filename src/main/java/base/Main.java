package base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import utils.ConfigReader;
import utils.ExtentManager;

import java.lang.reflect.Method;
import java.util.Map;

public class Main {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driver.get();
    }

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setup(Method method, Object[] testData) {

        String testName = method.getName();
        if (testData.length > 0 && testData[0] instanceof Map) {
            Map<String, String> map = (Map<String, String>) testData[0];
            testName += " - " + map.getOrDefault("UserName", "DataRow");
        }

        ExtentTest currentTest = ExtentManager.getInstance().createTest(testName);
        ExtentManager.setTest(currentTest);

        String browser = System.getProperty("browser");
        if (browser == null) {
            browser = ConfigReader.getProperty("browser");
        }
        browser = browser.toLowerCase();

        // 🛡️ NEW: READ HEADLESS FLAG FROM CONFIG
        String headless = ConfigReader.getProperty("headless");
        boolean isHeadless = headless != null && headless.equalsIgnoreCase("true");

        ExtentManager.getTest().log(Status.INFO, "Starting thread: " + Thread.currentThread().getId()
                + " | Browser: " + browser + " | Headless: " + isHeadless);

        WebDriver localDriver;
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (isHeadless) ffOptions.addArguments("-headless");
                localDriver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions eOptions = new EdgeOptions();
                if (isHeadless) eOptions.addArguments("--headless=new");
                localDriver = new EdgeDriver(eOptions);
                break;

            default: // Chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions cOptions = new ChromeOptions();
                if (isHeadless) {
                    // "--headless=new" is the modern version for Chrome
                    cOptions.addArguments("--headless=new");
                    // These 2 flags help stability in container/server environments
                    cOptions.addArguments("--disable-gpu");
                    cOptions.addArguments("--window-size=1920,1080");
                }
                localDriver = new ChromeDriver(cOptions);
                break;
        }

        driver.set(localDriver);
        getDriver().get(ConfigReader.getProperty("url"));
        // Window maximize is sometimes ignored in headless, but kept for regular mode
        getDriver().manage().window().maximize();
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        if (getDriver() != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                String base64 = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
                ExtentManager.getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());
                ExtentManager.getTest().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                ExtentManager.getTest().log(Status.PASS, "Test execution successful.");
            }
            getDriver().quit();
        }
        driver.remove();
        ExtentManager.removeTest();
        ExtentManager.getInstance().flush();
    }
}