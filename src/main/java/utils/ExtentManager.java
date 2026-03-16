package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;
    // This is the key to fixing your report!
    private static ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("Reports/AutomationReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static void setTest(ExtentTest test) {
        methodTest.set(test);
    }

    public static ExtentTest getTest() {
        return methodTest.get();
    }

    public static void removeTest() {
        methodTest.remove();
    }
}