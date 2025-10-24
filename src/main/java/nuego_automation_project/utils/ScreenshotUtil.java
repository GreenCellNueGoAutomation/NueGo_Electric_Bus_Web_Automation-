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

public class ScreenshotUtil {

    private ScreenshotUtil() {}

    public static String capture(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "screenshots/" + testName + "_" + timestamp + ".png";

        try {
            File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(fileName);
            dest.getParentFile().mkdirs();
            Files.copy(scr.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📸 Screenshot saved: " + fileName);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
