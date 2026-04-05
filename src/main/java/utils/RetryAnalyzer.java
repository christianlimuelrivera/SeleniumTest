package utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * SmartRetryAnalyzer - High-Efficiency Safety Net
 * Only retries for known "Flaky" Selenium exceptions.
 * Skips retries for AssertionErrors (Logic bugs).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {
        Throwable cause = result.getThrowable();

        // 🛑 CRITICAL CHECK: If the test failed because of a hard assertion (Assert.assertEquals),
        // it means the data is wrong. Retrying won't fix a wrong name or a math error.
        if (cause instanceof AssertionError) {
            System.out.println("❌ Logic Failure: Skipping retry for " + result.getName());
            return false;
        }

        // ✅ FLAKY CHECK: Only retry if it's a common "environmental" Selenium exception
        if (cause instanceof NoSuchElementException ||
                cause instanceof TimeoutException ||
                cause instanceof StaleElementReferenceException) {

            if (retryCount < MAX_RETRY_COUNT) {
                retryCount++;
                System.out.println("⚠️ Environmental Flake Detected. Retrying " + result.getName() +
                        " [Attempt " + (retryCount + 1) + "]");
                return true;
            }
        }

        // Default: If it's some other weird error, stop retrying to save execution time
        return false;
    }
}