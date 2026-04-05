package utils;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * SuiteListener - Report Finalizer
 *
 * Registered via @Listeners(SuiteListener.class) on Main.java.
 * onFinish() fires exactly once after ALL tests and ALL data rows
 * have completed — whether running from IDE, testng.xml, or mvn test.
 *
 * This replaces the old flush() call in Main.teardown(), which fired
 * after every single row and risked parallel file-write conflicts.
 */
public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // Nothing needed here — ExtentManager initializes lazily on first use.
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentManager.getInstance().flush();
    }
}