package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * 📊 EXTENTMANAGER - The Reporting Engine
 * This class creates and manages the HTML report.
 */
public class ExtentManager {


    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();

    /**
     * 🏗️ GET INSTANCE: Setup the physical report file.
     * Includes System Metadata for professional audit trails.
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            // 1️⃣ Initialize the Spark Reporter
            ExtentSparkReporter spark = new ExtentSparkReporter("Reports/AutomationReport.html");

            // 2️⃣ --- PROFESSIONAL CONFIGURATION ---
            spark.config().setDocumentTitle("DemoQA Regression Report"); // Browser Tab Title
            spark.config().setReportName("Selenium Testing"); // Header Name
            spark.config().setTheme(Theme.DARK); // Set to Dark Theme (Pro Standard)
            spark.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

            // 3️⃣ Attach Reporter
            extent = new ExtentReports();
            extent.attachReporter(spark);

            // 4️⃣ --- SYSTEM METADATA (Stakeholder Evidence) ---
            // These appear on the Dashboard tab of your report.
            extent.setSystemInfo("Tester", "Automation Engineer");
            extent.setSystemInfo("Environment", "QA-Sandbox");
            extent.setSystemInfo("Browser", "Cross-Browser Compatible");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Database", "SQLite - FormTest Table");
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