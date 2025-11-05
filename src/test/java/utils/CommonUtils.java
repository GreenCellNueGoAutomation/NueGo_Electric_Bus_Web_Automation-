package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CommonUtils {

    /**
     * ✅ Capture any visible error message shown on the web page.
     * This method is generic — it automatically detects and returns
     * the first visible text message found in common error/toast/dialog elements.
     */
    public static String captureErrorMessage(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

            // ✅ Common message locators (toast, alert, popup, etc.)
            By[] possibleLocators = {
                    By.xpath("//*[contains(@class,'error') or contains(@class,'toast') or contains(@class,'snackbar')]"),
                    By.xpath("//*[contains(@class,'alert') or contains(@class,'msg') or contains(@class,'message')]"),
                    By.xpath("//*[contains(text(),'error') or contains(text(),'Error') or contains(text(),'failed')]"),
                    By.xpath("//p | //span | //div") // fallback: any text element
            };

            for (By locator : possibleLocators) {
                List<WebElement> elements = driver.findElements(locator);
                for (WebElement el : elements) {
                    try {
                        if (el.isDisplayed()) {
                            String text = el.getText().trim();
                            if (!text.isEmpty() && text.length() < 300) {
                                return text;
                            }
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }
            }

            // ✅ Check for JavaScript alert message
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                String alertText = alert.getText();
                alert.accept();
                return alertText;
            } catch (TimeoutException ignored) {}

        } catch (Exception e) {
            System.out.println("⚠️ Unable to capture error message: " + e.getMessage());
        }

        return null; // No message found
    }
}
