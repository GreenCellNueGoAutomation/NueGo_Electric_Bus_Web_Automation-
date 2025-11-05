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
            // ✅ Handle Discount Alert Popup if present before proceeding
            handleDiscountPopup();

            // ✅ Scroll to Review Booking section first
            scrollToReviewSection();

            // ✅ Click on Coupon icon
            By couponLocator = By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
            safeClick(couponLocator, "Clicked on Coupon icon");

            // ✅ Wait for coupon modal to open and scroll inside it
            WebElement modalBody = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'modal-body')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", modalBody);
            System.out.println("✅ Scrolled inside coupon modal");

            // ✅ Apply the coupon (use dynamic locator if needed)
            By applyLocator = By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']/div[@class='review-payment']/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']/div[@class='modal-content']/div[@class='modal-body']/div[@class='content-section p-3']/div[@class='coupon-list-component p-3']/div[@class='listing']/div[3]/div[2]/div[1]/p[1]");
            safeClick(applyLocator, "Clicked Apply Coupon");

            // ✅ Wait for coupon success message or fallback
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

    // ---------------- Handle Popups (like Discount Alert!) ---------------- //
    public void handleDiscountPopup() {
        try {
            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(6));
            WebElement popup = popupWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Discount Alert!')]")));

            if (popup.isDisplayed()) {
                System.out.println("💡 Discount Alert popup detected!");

                // Prefer "No, Thanks" to continue flow
                By noThanks = By.xpath("//button[contains(text(),'No, Thanks')]");
                safeClick(noThanks, "Clicked 'No, Thanks' on Discount Alert popup");
                sleep(1000);
            }
        } catch (TimeoutException te) {
            System.out.println("ℹ️ No Discount Alert popup appeared.");
        } catch (Exception e) {
            System.out.println("⚠️ Issue handling Discount Alert popup: " + e.getMessage());
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

            // ✅ Handle popup if it appears after clicking Proceed
            handleBookingPopupIfPresent();

        } catch (Exception e) {
            System.out.println("❌ Unable to click Proceed & Book: " + e.getMessage());
        }
    }

    // ---------------- Handle the “Thank” Popup after Proceed ---------------- //
    public void handleBookingPopupIfPresent() {
        try {
            By popupLocator = By.xpath("//div[@class='no-switch-btn cursor-pointer open-600w-16s-24h']");
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            WebElement popup = shortWait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator));
            String popupText = popup.getText().trim();
            System.out.println("📢 Popup detected: " + popupText);

            popup.click();
            System.out.println("✅ Clicked popup (‘Thank’) button");

            // Re-click Proceed & Book after popup
            By proceedLocator = By.xpath("//button[@class='teal-22BBB0-bg cursor-pointer white-color submit-button text-center open-600w-16s-24h py-3']");
            waitAndScrollToElement(proceedLocator);
            safeClick(proceedLocator, "Clicked Proceed & Book again after popup");
            System.out.println("🎯 Popup handled and re-clicked Proceed & Book");

        } catch (TimeoutException te) {
            System.out.println("ℹ️ No extra popup appeared after Proceed & Book");
        } catch (Exception e) {
            System.out.println("⚠️ Error while handling post-proceed popup: " + e.getMessage());
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
                System.out.println("⚡ JS clicked - " + logMessage);
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
