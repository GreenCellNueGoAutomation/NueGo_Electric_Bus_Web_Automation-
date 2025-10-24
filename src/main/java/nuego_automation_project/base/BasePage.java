package nuego_automation_project.base;

import nuego_automation_project.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;

/**
 * BasePage class for common page methods
 */
public abstract class BasePage {

    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Common method to take screenshot for all pages
     *
     * @param name screenshot file name prefix
     */
    protected void takeScreenshot(String name) {
        ScreenshotUtil.capture(driver, name);
    }
}
