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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25)); // ⏳ Increased global wait
    }

    // ---------------- Scroll to Review Booking Section ---------------- //
    public void scrollToReviewSection() {
        WebElement reviewSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[normalize-space()='Review Booking']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", reviewSection);
        sleep(1000);
        System.out.println("✅ Scrolled to Review Booking section");
    }

    // ---------------- Coupon Actions ---------------- //
    public void clickCouponButton() {
        By couponLocator = By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
        WebElement couponButton = wait.until(ExpectedConditions.elementToBeClickable(couponLocator));
        safeClick(couponButton);
        System.out.println("✅ Coupon button clicked");
        sleep(1000);
    }

    public void scrollCouponModal() {
        WebElement modalBody = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'modal-body')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", modalBody);
        sleep(1000);
        System.out.println("✅ Scrolled inside coupon modal");
    }

    public void clickApplyCoupon() {
        By applyLocator = By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']" +
                "/div[@class='review-payment']/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']" +
                "/div[@class='modal-content']/div[@class='modal-body']/div[@class='content-section p-3']" +
                "/div[@class='coupon-list-component p-3']/div[@class='listing']/div[3]/div[2]/div[1]/p[1]");
        WebElement applyCoupon = wait.until(ExpectedConditions.elementToBeClickable(applyLocator));
        safeClick(applyCoupon);
        System.out.println("✅ Applied coupon");

        // Wait for Passenger Section
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Select from list']")));
        System.out.println("⏳ Passenger selection section loaded");
        sleep(1000);
    }

    // ---------------- Passenger Selection from List ---------------- //
    public void selectPassengerFromList() {
        // 1️⃣ Wait & Click "Select from list"
        By selectFromListLocator = By.xpath("//div[@class='d-flex justify-content-between mb-3']//p[@class='open-600w-16s-24h teal-2-00A095-color cursor-pointer'][normalize-space()='Select from list']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(selectFromListLocator));
        WebElement selectFromList = wait.until(ExpectedConditions.elementToBeClickable(selectFromListLocator));
        safeClick(selectFromList);
        System.out.println("✅ Clicked on 'Select from list'");
        sleep(1000);

        // 2️⃣ Wait & Click checkbox for 'Rutuja'
        By checkboxLocator = By.xpath("//p[contains(@class,'black-1E1E26-color') and normalize-space(text())='Rutuja']");
        wait.until(ExpectedConditions.presenceOfElementLocated(checkboxLocator));
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));
        safeClick(checkbox);
        System.out.println("✅ Selected passenger: Rutuja");
        sleep(1000);

        // 3️⃣ Wait & Click 'Add Passenger' button
        By addPassengerLocator = By.xpath("//button[@class='submit-button mt-5']");
        wait.until(ExpectedConditions.presenceOfElementLocated(addPassengerLocator));
        WebElement addPassengerBtn = wait.until(ExpectedConditions.elementToBeClickable(addPassengerLocator));
        safeClick(addPassengerBtn);
        System.out.println("✅ Added passenger from list");

        // Confirm Passenger added
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addPassengerLocator));
        System.out.println("🟢 Passenger successfully added and modal closed");
        sleep(1000);
    }

    // ---------------- Assurance & Wallet ---------------- //
    public void clickAssuranceCheckbox() {
        By checkboxLocator = By.xpath("//img[contains(@class,'opacity-100')]");
        WebElement assurance = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));
        safeClick(assurance);
        System.out.println("✅ NueGo Assurance checkbox clicked");
        sleep(1000);
    }

    public void clickWalletApply() {
        By walletLocator = By.xpath("//p[normalize-space()='Apply']");
        WebElement wallet = wait.until(ExpectedConditions.elementToBeClickable(walletLocator));
        safeClick(wallet);
        System.out.println("✅ Wallet balance applied");
        sleep(1000);
    }

    // ---------------- Proceed to Book ---------------- //
    public void clickProceedToBook() {
        // Ensure passenger added before proceeding
        selectPassengerFromList();

        By proceedLocator = By.xpath("//button[contains(@class,'teal-22BBB0-bg')]");
        wait.until(ExpectedConditions.presenceOfElementLocated(proceedLocator));
        WebElement proceedBtn = wait.until(ExpectedConditions.elementToBeClickable(proceedLocator));
        safeClick(proceedBtn);
        System.out.println("✅ Clicked Proceed & Book");
        sleep(1500);
    }

    // ---------------- Utility Methods ---------------- //
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
