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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Scroll to Review Booking Section
    public void scrollToReviewSection() {
        WebElement reviewSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[normalize-space()='Review Booking']")));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", reviewSection);
        sleep(1000);
        System.out.println("✅ Scrolled to Review Booking section");
    }

    // Click coupon button
    public void clickCouponButton() {
        By couponLocator = By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
        WebElement couponButton = wait.until(ExpectedConditions.elementToBeClickable(couponLocator));
        safeClick(couponButton);
        System.out.println("✅ Coupon button clicked");
        sleep(1000);
    }

    // Scroll coupon modal
    public void scrollCouponModal() {
        WebElement modalBody = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'modal-body')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", modalBody);
        sleep(1000);
        System.out.println("✅ Scrolled inside coupon modal");
    }

    // Click apply coupon
    public void clickApplyCoupon() {
        By applyLocator = By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']" +
                "/div[@class='review-payment']/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']" +
                "/div[@class='modal-content']/div[@class='modal-body']/div[@class='content-section p-3']" +
                "/div[@class='coupon-list-component p-3']/div[@class='listing']/div[3]/div[2]/div[1]/p[1]");
        WebElement applyCoupon = wait.until(ExpectedConditions.elementToBeClickable(applyLocator));
        safeClick(applyCoupon);
        System.out.println("✅ Applied coupon");
        sleep(1000);
    }

    // Click assurance checkbox
    public void clickAssuranceCheckbox() {
        By checkboxLocator = By.xpath("//img[contains(@class,'opacity-100')]");
        WebElement assurance = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));
        safeClick(assurance);
        System.out.println("✅ NueGo Assurance checkbox clicked");
        sleep(1000);
    }

    // Apply wallet balance
    public void clickWalletApply() {
        By walletLocator = By.xpath("//p[normalize-space()='Apply']");
        WebElement wallet = wait.until(ExpectedConditions.elementToBeClickable(walletLocator));
        safeClick(wallet);
        System.out.println("✅ Wallet balance applied");
        sleep(1000);
    }

    // Click Proceed to Book
    public void clickProceedToBook() {
        By proceedLocator = By.xpath("//button[contains(@class,'teal-22BBB0-bg')]");
        WebElement proceedBtn = wait.until(ExpectedConditions.elementToBeClickable(proceedLocator));
        safeClick(proceedBtn);
        System.out.println("✅ Clicked Proceed & Book");
        sleep(1500);
    }

    // ---------- Dynamic Wait Helper ----------
    public void waitForElementBeforeClick(String elementName) {
        try {
            By locator;
            switch (elementName) {
                case "couponButton":
                    locator = By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
                    break;
                case "applyCoupon":
                    locator = By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']/div[@class='review-payment']/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']/div[@class='modal-content']/div[@class='modal-body']/div[@class='content-section p-3']/div[@class='coupon-list-component p-3']/div[@class='listing']/div[3]/div[2]/div[1]/p[1]");
                    break;
                case "assuranceCheckbox":
                    locator = By.xpath("//img[contains(@class,'opacity-100')]");
                    break;
                case "walletApply":
                    locator = By.xpath("//p[normalize-space()='Apply']");
                    break;
                case "proceedToBook":
                    locator = By.xpath("//button[contains(@class,'teal-22BBB0-bg')]");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown element name: " + elementName);
            }

            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            wait.until(ExpectedConditions.visibilityOf(el));
            System.out.println("✅ Element ready: " + elementName);
            sleep(500);

        } catch (Exception e) {
            throw new RuntimeException("⚠️ Wait failed for: " + elementName + " - " + e.getMessage());
        }
    }

    // ---------- Utility ----------
    private void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
