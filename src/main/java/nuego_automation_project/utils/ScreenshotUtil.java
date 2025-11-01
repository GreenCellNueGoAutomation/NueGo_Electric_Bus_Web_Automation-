package nuego_automation_project.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing Selenium screenshots.
 * Saves images under <project_root>/screenshots directory.
 */
public class ScreenshotUtil {

    private ScreenshotUtil() {
        // Prevent instantiation
    }

    /**
     * Captures a screenshot and saves it with test name + timestamp.
     *
     * @param driver   The active WebDriver instance.
     * @param testName The name of the test (used in file naming).
     * @return Absolute path of saved screenshot (null if failed).
     */
    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Save inside project directory, always absolute path
        String screenshotsDir = System.getProperty("user.dir") + File.separator + "screenshots";
        String filePath = screenshotsDir + File.separator + testName + "_" + timestamp + ".png";

        try {
            // Ensure directory exists
            File dir = new File(screenshotsDir);
            if (!dir.exists() && !dir.mkdirs()) {
                System.out.println("⚠️ Failed to create screenshots directory: " + dir.getAbsolutePath());
                return null;
            }

            // Capture and save
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(filePath);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("📸 Screenshot saved successfully: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();

        } catch (IOException e) {
            System.out.println("❌ Failed to capture screenshot (IO error): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected error while taking screenshot: " + e.getMessage());
        }

        return null;
    }
}
