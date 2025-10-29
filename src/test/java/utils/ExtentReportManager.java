package utils;  

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports getReport() {
        if (extent == null) {
            // ✅ Generate a unique timestamp for each report
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/target/ExtentReports/ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setReportName("NueGo Automation Report");
            reporter.config().setDocumentTitle("Test Results");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Tester", "Sumedh Sonawane");
        }
        return extent;
    }
}
