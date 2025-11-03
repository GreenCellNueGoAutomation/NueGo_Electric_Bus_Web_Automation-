package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.*;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentListener implements ITestListener {

    private static ExtentReports extent = ExtentReportManager.getReport();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("✅ Test Passed Successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object currentClass = result.getInstance();
        WebDriver driver = null;

        try {
            driver = (WebDriver) currentClass.getClass().getSuperclass()
                    .getDeclaredField("driver").get(currentClass);
        } catch (Exception e) {
            test.get().fail("❌ Failed to access WebDriver for screenshot: " + e.getMessage());
        }

        String methodName = result.getMethod().getMethodName();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = System.getProperty("user.dir") + "/target/screenshots/" 
                                + methodName + "_" + timeStamp + ".png";

        if (driver != null) {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                File dest = new File(screenshotPath);
                FileUtils.copyFile(src, dest);
                test.get().fail("❌ Test Failed: " + result.getThrowable().getMessage(),
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (IOException e) {
                test.get().fail("⚠️ Screenshot capture failed: " + e.getMessage());
            }
        } else {
            test.get().fail("⚠️ WebDriver is null, cannot capture screenshot");
        }

        // Log exception details
        test.get().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("⚠️ Test Skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
