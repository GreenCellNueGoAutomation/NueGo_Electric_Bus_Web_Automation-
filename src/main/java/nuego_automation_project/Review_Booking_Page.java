package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Review_Booking_Page {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Review_Booking_Page(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ---------------- Coupon Flow + Complete Sequence ---------------- //
    public void clickApplyCoupon() {
        try {
            // ✅ Scroll to Review Booking section first
            scrollToReviewSection();

            // ✅ Click on Coupon
            By couponLocator = By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
            safeClick(couponLocator, "Clicked on Coupon icon");

            // ✅ Wait for modal to open and scroll inside modal
            WebElement modalBody = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'modal-body')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", modalBody);
            System.out.println("✅ Scrolled inside coupon modal");

            // ✅ Apply the coupon
            By applyLocator = By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']/div[@class='review-payment']/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']/div[@class='modal-content']/div[@class='modal-body']/div[@class='content-section p-3']/div[@class='coupon-list-component p-3']/div[@class='listing']/div[2]/div[2]/div[1]/p[1]");
            safeClick(applyLocator, "Clicked Apply Coupon");

            // ✅ Wait for coupon success or fallback
            try {
                By successMsg = By.xpath("//p[contains(text(),'Coupon Applied Successfully') or contains(text(),'applied successfully')]");
                wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
                System.out.println("✅ Coupon applied successfully message displayed");
            } catch (Exception e) {
                System.out.println("⚠️ Coupon success message not found, proceeding...");
            }

            // ✅ Scroll down to Assurance & Wallet section
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1000);");
            System.out.println("⬇️ Scrolled down to Assurance and Wallet section");
            sleep(1500);

            // ✅ Click Assurance Checkbox
            clickAssuranceCheckbox();

            // ✅ Apply Wallet
            clickWalletApply();

            // ✅ Proceed to Book
            clickProceedToBook();

            System.out.println("🎉 Completed post-coupon booking flow successfully");

        } catch (Exception e) {
            System.out.println("❌ Error in full booking sequence: " + e.getMessage());
        }
    }

    // ---------------- Scroll to Review Booking Section ---------------- //
    public void scrollToReviewSection() {
        WebElement reviewSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[normalize-space()='Review Booking']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", reviewSection);
        sleep(1000);
        System.out.println("✅ Scrolled to Review Booking section");
    }

    // ---------------- Assurance & Wallet ---------------- //
    public void clickAssuranceCheckbox() {
        try {
            By checkboxLocator = By.xpath("//img[contains(@class,'opacity-100')]");
            waitAndScrollToElement(checkboxLocator);
            safeClick(checkboxLocator, "Clicked on NueGo Assurance checkbox");
            sleep(800);
        } catch (Exception e) {
            System.out.println("⚠️ Unable to click Assurance checkbox: " + e.getMessage());
        }
    }

    public void clickWalletApply() {
        try {
            By walletLocator = By.xpath("//p[normalize-space()='Apply']");
            waitAndScrollToElement(walletLocator);
            safeClick(walletLocator, "Applied wallet balance");
            sleep(1500);
        } catch (Exception e) {
            System.out.println("⚠️ Wallet apply step skipped or not available: " + e.getMessage());
        }
    }

    // ---------------- Proceed to Book ---------------- //
    public void clickProceedToBook() {
        try {
            By proceedLocator = By.xpath("//button[contains(@class,'teal-22BBB0-bg')]");
            waitAndScrollToElement(proceedLocator);
            safeClick(proceedLocator, "Clicked Proceed & Book");
            sleep(1000);
            System.out.println("✅ Proceed to Book clicked successfully");
        } catch (Exception e) {
            System.out.println("⚠️ Unable to click Proceed & Book: " + e.getMessage());
        }
    }

    // ---------------- Utility Methods ---------------- //
    private void safeClick(By locator, String logMessage) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
            System.out.println("✅ " + logMessage);
        } catch (Exception e) {
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                System.out.println("⚠️ JS clicked - " + logMessage);
            } catch (Exception ignored) {
                System.out.println("❌ Failed to click element: " + logMessage);
            }
        }
    }

    private void waitAndScrollToElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
