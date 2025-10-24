package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Filters {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Constructor
    public Filters(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ---------------- Filter Actions ---------------- //

    // 1. Select timer and return its label/value
    public String selectTimer() {
        By timer = By.xpath("//div[6]//img[1]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(timer));
        safeClickWithRetry(timer, "Timer selected");
        pause(500);

        By label = By.xpath("//div[6]//p");
        wait.until(ExpectedConditions.visibilityOfElementLocated(label));
        return getElementTextSafe(label, "Timer");
    }

    // 2. Click Boarding Point (expand dropdown)
    public void clickBoardingPoint() {
        By boardingPoint = By.xpath("//p[normalize-space()='Search Boarding Point']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(boardingPoint));
        safeClickWithRetry(boardingPoint, "Boarding Point clicked");
        pause(500);
    }

    // 3. Select Boarding Point Checkbox and return name
    public String selectBoardingCheckbox() {
        By boardingCheckbox = By.xpath("//div[@class='position-relative']//div[4]//img[1]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(boardingCheckbox));
        safeClickWithRetry(boardingCheckbox, "Boarding Point Checkbox selected");
        pause(500);

        By pointName = By.xpath("//div[@class='position-relative']//div[4]//p");
        wait.until(ExpectedConditions.visibilityOfElementLocated(pointName));
        return getElementTextSafe(pointName, "Boarding Point");
    }

    // 4. Click Dropping Point (expand dropdown)
    public void clickDroppingPoint() {
        By droppingPoint = By.xpath("//p[normalize-space()='Search Dropping Point']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(droppingPoint));
        safeClickWithRetry(droppingPoint, "Dropping Point clicked");
        pause(500);
    }

    // 5+6+7: Select Dropping Point Checkbox → scroll up → Reset
    public String selectDroppingCheckboxAndReset() {
        By droppingCheckbox = By.xpath("//div[10]//div[1]//div[2]//div[1]//img[1]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(droppingCheckbox));
        safeClickWithRetry(droppingCheckbox, "Dropping Point Checkbox selected");
        pause(500);

        // Get selected name before reset
        By pointName = By.xpath("//div[@class='position-relative']//div[4]//p");
        wait.until(ExpectedConditions.visibilityOfElementLocated(pointName));
        String selectedName = getElementTextSafe(pointName, "Dropping Point");

        // Scroll up
        scrollUpLikeHuman();
        pause(800);

        // Reset
        By resetAll = By.xpath("//p[@class='teal-22BBB0-color open-600w-16s-24h cursor-pointer mb-0']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(resetAll));
        safeClickWithRetry(resetAll, "Reset All clicked");
        pause(500);

        return selectedName;
    }

    // ---------------- Utility Methods ---------------- //

    private WebElement safeClickWithRetry(By locator, String message) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                smoothScroll(element);
                element.click();
                System.out.println("✅ " + message);
                return element;
            } catch (StaleElementReferenceException stale) {
                System.out.println("Retrying click: " + message);
                attempts++;
            } catch (Exception e) {
                try {
                    WebElement element = driver.findElement(locator);
                    smoothScroll(element);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    System.out.println("✅ " + message + " (JS Click)");
                    return element;
                } catch (Exception ex) {
                    System.out.println("❌ Failed to click: " + message);
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private String getElementTextSafe(By locator, String fallback) {
        int attempts = 0;
        while (attempts < 2) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                String text = element.getText().trim();
                return text.isEmpty() ? fallback : text;
            } catch (StaleElementReferenceException stale) {
                System.out.println("Retrying getText for " + fallback);
                attempts++;
            } catch (Exception e) {
                return fallback;
            }
        }
        return fallback;
    }

    private void smoothScroll(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", el
            );
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}
    }

    private void scrollUpLikeHuman() {
        try {
            Long currentScroll = (Long) ((JavascriptExecutor) driver).executeScript("return window.scrollY;");
            while (currentScroll > 0) {
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -120);");
                Thread.sleep(200);
                currentScroll = (Long) ((JavascriptExecutor) driver).executeScript("return window.scrollY;");
            }
            System.out.println("⬆️ Human-like scroll up completed");
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");
        }
    }

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
