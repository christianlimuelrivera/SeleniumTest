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

        // Create the test and store it in ExtentManager's ThreadLocal
        ExtentTest currentTest = ExtentManager.getInstance().createTest(testName);
        ExtentManager.setTest(currentTest);

        String browser = System.getProperty("browser", "chrome").toLowerCase();
        ExtentManager.getTest().log(Status.INFO, "Starting thread: " + Thread.currentThread().getId() + " | Browser: " + browser);

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
            default:
                WebDriverManager.chromedriver().setup();
                localDriver = new ChromeDriver();
                break;
        }

        driver.set(localDriver);
        getDriver().get("https://practicetestautomation.com/practice-test-login/");
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
        ExtentManager.removeTest(); // Clean up the report thread
        ExtentManager.getInstance().flush();
    }
}