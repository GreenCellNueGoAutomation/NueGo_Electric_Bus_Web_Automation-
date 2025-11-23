package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Simple RetryAnalyzer: retries a failed test up to maxRetryCount times.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int maxRetryCount = 1; // change as needed

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            System.out.println("🔁 Retrying test " + result.getName() + " — retry " + retryCount + "/" + maxRetryCount);
            return true;
        }
        return false;
    }
}
