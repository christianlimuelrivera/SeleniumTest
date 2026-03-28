package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * 📊 EXTENTMANAGER - The Reporting Engine
 * This class creates and manages the HTML report.
 */
public class ExtentManager {

    // 🏛️ The "Library": This object manages the entire HTML file itself.
    private static ExtentReports extent;

    // 🧵 The "Secret Weapon": ThreadLocal.
    // Imagine 2 reporters (threads) writing 2 different stories at the same time.
    // ThreadLocal gives each reporter their own private notebook so they don't
    // mix up their notes.
    private static ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();

    /**
     * 🏗️ GET INSTANCE: Setup the physical report file.
     * It only creates the file ONCE per test run (Singleton pattern).
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            // 1️⃣ Decide where the report lives (Reports folder) and what it's named.
            ExtentSparkReporter spark = new ExtentSparkReporter("Reports/AutomationReport.html");

            // 2️⃣ Attach the "Spark" reporter to the main ExtentReports object.
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    /**
     * 📥 SET TEST: Save the current test into the current thread's "notebook."
     * Called in Main.java @BeforeMethod.
     */
    public static void setTest(ExtentTest test) {
        methodTest.set(test);
    }

    /**
     * 📤 GET TEST: Grab the notebook for the current thread so we can add logs/screenshots.
     * This is how we write: ExtentManager.getTest().log(Status.PASS, "Message");
     */
    public static ExtentTest getTest() {
        return methodTest.get();
    }

    /**
     * 🧹 REMOVE TEST: Clear the "notebook" after the test is finished.
     * This prevents memory leaks and ensures a clean start for the next test.
     */
    public static void removeTest() {
        methodTest.remove();
    }
}