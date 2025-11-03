package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports getReport() {
        if (extent == null) {

            // 🧱 Get Jenkins build number, fallback to timestamp for local runs
            String buildNum = System.getenv("BUILD_NUMBER");
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if (buildNum == null || buildNum.isEmpty()) {
                buildNum = timestamp;
            }

            // 🗂 Define unique report path
            String reportPath = System.getProperty("user.dir") + "/target/ExtentReports/NueGo_Report_Build_" + buildNum + ".html";

            // 🧩 Configure Extent Spark Reporter
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setTheme(Theme.DARK);
            reporter.config().setDocumentTitle("🚀 NueGo Automation Report");
            reporter.config().setReportName("NueGo Web Regression Suite - Build #" + buildNum);
            reporter.config().setEncoding("utf-8");

            // 🎨 Custom CSS for better dark mode styling
            String customCSS =
                    "body { background-color: #121212; color: #f0f0f0; font-family: 'Segoe UI'; }" +
                    ".card-panel { border-radius: 10px; background: #1e1e1e; box-shadow: 0 4px 10px rgba(0,0,0,0.3); }" +
                    ".badge { border-radius: 8px; }" +
                    ".badge-success { background-color: #00C853; }" +
                    ".badge-danger { background-color: #FF1744; }" +
                    ".badge-warning { background-color: #FFAB00; color: #000; }" +
                    ".test-name { font-weight: bold; color: #00B8D4; }";

            reporter.config().setCss(customCSS);

            // 🔗 Attach reporter
            extent = new ExtentReports();
            extent.attachReporter(reporter);

            // 🧾 Add system info
            extent.setSystemInfo("Tester", "Sumedh Sonawane");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("Build Number", buildNum);
        }

        return extent;
    }
}
