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

    /* =========================================================
       HIGH LEVEL FLOW  (SAFE FOR NEW + RESCHEDULE)
       ========================================================= */
    public void completeReviewBookingFlow() {
        try {
            handleDiscountPopup();
            scrollToReviewSection();

            // 🔹 COUPON SECTION – ONLY IF PRESENT
            if (isElementPresent(getCouponIconLocator())) {
                System.out.println("🔎 Coupon section found – applying coupon flow");
                openCouponModal();
                applyThirdCouponFromList();
                waitForCouponSuccessMessageIfAny();
            } else {
                System.out.println("ℹ️ Coupon icon not present on this flow – skipping coupon steps");
            }

            // 🔹 ASSURANCE / WALLET / MILES – ONLY IF PRESENT
            scrollToAssuranceWalletSectionIfExists();

            if (isElementPresent(getAssuranceCheckboxLocator())) {
                clickAssuranceCheckbox();
            } else {
                System.out.println("ℹ️ Assurance checkbox not present – skipping Assurance step");
            }

            if (isElementPresent(getWalletApplyButtonLocator())) {
                clickWalletApply();
            } else {
                System.out.println("ℹ️ Wallet Apply button not present – skipping wallet step");
            }

            if (isElementPresent(getGreenMilesIconLocator())) {
                clickGreenMilesIcon();
                String milesBalance = getGreenMilesBalanceText();
                System.out.println("💰 Green Miles Balance: " + milesBalance);
            } else {
                System.out.println("ℹ️ Green Miles icon not present – skipping Miles step");
            }

            // 🔹 GST validation – only if labels exist
            if (isElementPresent(getBaseFareLabelLocator()) &&
                isElementPresent(getGstLabelLocator()) &&
                isElementPresent(getTotalFareLabelLocator())) {

                validateGstAfterDiscounts();
            } else {
                System.out.println("ℹ️ GST / fare labels missing – skipping GST validation");
            }

            clickProceedToBookButton();
            handleBookingPopupIfPresent();
        } catch (Exception e) {
            System.out.println("❌ Error in full booking sequence: " + e.getMessage());
        }
    }

    /* =========================================================
       POPUPS
       ========================================================= */

    public By getDiscountAlertPopupLocator() {
        return By.xpath("//div[contains(text(),'Discount Alert!')]");
    }

    public By getDiscountAlertNoThanksButtonLocator() {
        return By.xpath("//button[contains(text(),'No, Thanks')]");
    }

    public void handleDiscountPopup() {
        try {
            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(6));
            WebElement popup = popupWait.until(
                    ExpectedConditions.visibilityOfElementLocated(getDiscountAlertPopupLocator())
            );

            if (popup.isDisplayed()) {
                System.out.println("💡 Discount Alert popup detected!");
                safeClick(getDiscountAlertNoThanksButtonLocator(), "Clicked 'No, Thanks' on Discount Alert popup");
                sleep(500);
            }
        } catch (TimeoutException te) {
            System.out.println("ℹ️ No Discount Alert popup appeared.");
        } catch (Exception e) {
            System.out.println("⚠️ Issue handling Discount Alert popup: " + e.getMessage());
        }
    }

    /* =========================================================
       REVIEW BOOKING SECTION
       ========================================================= */

    public By getReviewBookingSectionTextLocator() {
        return By.xpath("//p[normalize-space()='Review Booking']");
    }

    public void scrollToReviewSection() {
        WebElement reviewSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(getReviewBookingSectionTextLocator())
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", reviewSection
        );
        sleep(500);
        System.out.println("✅ Scrolled to Review Booking section");
    }

    /* =========================================================
       COUPON ICON + MODAL
       ========================================================= */

    public By getCouponIconLocator() {
        return By.xpath("//div[contains(@class,'coupon-dashed-box')]//img[contains(@alt,'alt')]");
    }

    public By getCouponModalBodyLocator() {
        return By.xpath("//div[contains(@class,'modal-body')]");
    }

    public void clickCouponIcon() {
        safeClick(getCouponIconLocator(), "Clicked on Coupon icon");
    }

    public WebElement waitForCouponModal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(getCouponModalBodyLocator()));
    }

    public void openCouponModal() {
        try {
            clickCouponIcon();
            WebElement modalBody = waitForCouponModal();
            System.out.println("✅ Coupon modal opened");

            // scroll inside modal if needed
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollTop = arguments[0].scrollHeight;", modalBody
            );
        } catch (TimeoutException te) {
            System.out.println("⚠️ Coupon modal did not appear, skipping coupon flow. " + te.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error in openCouponModal: " + e.getMessage());
        }
    }

    /* =========================================================
       APPLY COUPON
       ========================================================= */

    public By getThirdCouponApplyLocator() {
        return By.xpath(
                "//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']/div[@class='review-payment']" +
                        "/div[@class='coupon-list-modal']/div[@role='dialog']/div[@role='document']/div[@class='modal-content']" +
                        "/div[@class='modal-body']/div[@class='content-section p-3']/div[@class='coupon-list-component p-3']" +
                        "/div[@class='listing']/div[3]/div[2]/div[1]/p[1]"
        );
    }

    public By getCouponSuccessMessageLocator() {
        return By.xpath("//p[contains(text(),'Coupon Applied Successfully') or contains(text(),'applied successfully')]");
    }

    public void applyThirdCouponFromList() {
        try {
            if (isElementPresent(getThirdCouponApplyLocator())) {
                safeClick(getThirdCouponApplyLocator(), "Clicked Apply on 3rd coupon");
            } else {
                System.out.println("ℹ️ 3rd coupon not present – skipping coupon apply");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error while applying 3rd coupon: " + e.getMessage());
        }
    }

    public void waitForCouponSuccessMessageIfAny() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(getCouponSuccessMessageLocator()));
            System.out.println("✅ Coupon applied successfully message displayed");
        } catch (Exception e) {
            System.out.println("⚠️ Coupon success message not found, proceeding...");
        }
    }

    /* =========================================================
       ASSURANCE / WALLET / MILES SECTION
       ========================================================= */

    public By getAssuranceTextLocator() {
        return By.xpath("//p[contains(text(),'NueGo Assurance') or contains(text(),'Assurance')]");
    }

    public void scrollToAssuranceWalletSectionIfExists() {
        try {
            if (isElementPresent(getAssuranceTextLocator())) {
                WebElement assuranceElement = wait.until(
                        ExpectedConditions.presenceOfElementLocated(getAssuranceTextLocator())
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", assuranceElement
                );
                sleep(500);
                System.out.println("⬇️ Scrolled to Assurance / Wallet / Miles section");
            } else {
                System.out.println("ℹ️ Assurance / Wallet section not present – skipping scroll");
            }
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
            sleep(500);
            System.out.println("⬇️ Fallback scroll to bottom section");
        }
    }

    public By getAssuranceCheckboxLocator() {
        return By.xpath("//img[contains(@class,'opacity-100')]");
    }

    public void clickAssuranceCheckbox() {
        try {
            waitAndScrollToElement(getAssuranceCheckboxLocator());
            safeClick(getAssuranceCheckboxLocator(), "Clicked on NueGo Assurance checkbox");
            sleep(300);
        } catch (Exception e) {
            System.out.println("⚠️ Unable to click Assurance checkbox: " + e.getMessage());
        }
    }

    public By getWalletApplyButtonLocator() {
        return By.xpath("//p[normalize-space()='Apply']");
    }

    public void clickWalletApply() {
        try {
            waitAndScrollToElement(getWalletApplyButtonLocator());
            safeClick(getWalletApplyButtonLocator(), "Applied wallet balance");
            sleep(800);
        } catch (Exception e) {
            System.out.println("⚠️ Wallet apply step skipped or not available: " + e.getMessage());
        }
    }

    public By getGreenMilesIconLocator() {
        return By.xpath("//div[@class='d-flex gap-12']//img[@alt='alt']");
    }

    public By getGreenMilesBalanceTextLocator() {
        return By.xpath("//p[contains(text(),'Miles') or contains(text(),'mile') or contains(text(),'Balance')]");
    }

    public void clickGreenMilesIcon() {
        try {
            waitAndScrollToElement(getGreenMilesIconLocator());
            safeClick(getGreenMilesIconLocator(), "Clicked on Green Miles icon");
        } catch (Exception e) {
            System.out.println("❌ Error while clicking Green Miles icon: " + e.getMessage());
        }
    }

    public String getGreenMilesBalanceText() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement milesBalanceElement = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(getGreenMilesBalanceTextLocator())
            );
            return milesBalanceElement.getText().trim();
        } catch (TimeoutException te) {
            System.out.println("ℹ️ Green Miles balance not visible: " + te.getMessage());
            return "";
        } catch (Exception e) {
            System.out.println("❌ Error while reading Green Miles balance: " + e.getMessage());
            return "";
        }
    }

    /* =========================================================
       GST LABELS + AMOUNTS
       ========================================================= */

    public By getBaseFareLabelLocator() {
        return By.xpath("//p[normalize-space()='Base Fare']");
    }

    public By getGstLabelLocator() {
        return By.xpath("//p[normalize-space()='GST']");
    }

    public By getTotalFareLabelLocator() {
        return By.xpath("//p[normalize-space()='Total Fare']");
    }

    public WebElement getBaseFareAmountElement() {
        WebElement baseFareText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(getBaseFareLabelLocator())
        );
        return baseFareText.findElement(By.xpath("./following-sibling::p[1]"));
    }

    public WebElement getGstAmountElement() {
        WebElement gstText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(getGstLabelLocator())
        );
        return gstText.findElement(By.xpath("./following-sibling::p[1]"));
    }

    public WebElement getTotalFareAmountElement() {
        WebElement totalFareText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(getTotalFareLabelLocator())
        );
        return totalFareText.findElement(By.xpath("./following-sibling::p[1]"));
    }

    public double getBaseFareAmount() {
        return parseAmount(getBaseFareAmountElement().getText());
    }

    public double getGstAmount() {
        return parseAmount(getGstAmountElement().getText());
    }

    public double getTotalFareAmount() {
        return parseAmount(getTotalFareAmountElement().getText());
    }

    public void validateGstAfterDiscounts() {
        try {
            double baseFare  = getBaseFareAmount();
            double gstAmount = getGstAmount();
            double totalFare = getTotalFareAmount();

            System.out.println("💵 Base Fare:  " + baseFare);
            System.out.println("🧾 GST Amount: " + gstAmount);
            System.out.println("📦 Total Fare: " + totalFare);

            if (baseFare <= 0) {
                System.out.println("⚠️ Base fare is zero or negative, skipping GST validation.");
                return;
            }

            double gstPercent = (gstAmount / baseFare) * 100.0;
            System.out.println("📊 Calculated GST%: " + gstPercent);

            double expectedTotal = baseFare + gstAmount;
            double difference = Math.abs(expectedTotal - totalFare);
            if (difference > 2.0) {
                System.out.println("⚠️ Total Fare mismatch! Expected ≈ " + expectedTotal + ", Found: " + totalFare);
            }

            double maxAllowedGstPercent = 18.0;
            if (gstPercent > maxAllowedGstPercent) {
                String msg = "❌ GST percentage too high! GST% = " + gstPercent + " > " + maxAllowedGstPercent;
                System.out.println(msg);
                throw new AssertionError(msg);
            } else {
                System.out.println("✅ GST percentage is within acceptable range (<= " + maxAllowedGstPercent + "%)");
            }

        } catch (Exception e) {
            System.out.println("❌ Error calculating/validating GST: " + e.getMessage());
        }
    }

    private double parseAmount(String text) {
        if (text == null) return 0.0;
        String cleaned = text.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Unable to parse amount from text: '" + text + "'");
            return 0.0;
        }
    }

    /* =========================================================
       PROCEED TO BOOK + POPUP
       ========================================================= */

    public By getProceedToBookButtonLocator() {
        return By.xpath("//button[contains(@class,'teal-22BBB0-bg')]");
    }

    public By getThankPopupLocator() {
        return By.xpath("//div[@class='no-switch-btn cursor-pointer open-600w-16s-24h']");
    }

    public By getProceedToBookButtonAfterPopupLocator() {
        return By.xpath("//button[@class='teal-22BBB0-bg cursor-pointer white-color submit-button text-center open-600w-16s-24h py-3']");
    }

    public void clickProceedToBookButton() {
        try {
            waitAndScrollToElement(getProceedToBookButtonLocator());
            safeClick(getProceedToBookButtonLocator(), "Clicked Proceed & Book");
            sleep(500);
            System.out.println("✅ Proceed to Book clicked successfully");
        } catch (Exception e) {
            System.out.println("❌ Unable to click Proceed & Book: " + e.getMessage());
        }
    }

    public void handleBookingPopupIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement popup = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(getThankPopupLocator())
            );
            String popupText = popup.getText().trim();
            System.out.println("📢 Popup detected: " + popupText);

            popup.click();
            System.out.println("✅ Clicked popup (‘Thank’) button");

            waitAndScrollToElement(getProceedToBookButtonAfterPopupLocator());
            safeClick(getProceedToBookButtonAfterPopupLocator(),
                    "Clicked Proceed & Book again after popup");
            System.out.println("🎯 Popup handled and re-clicked Proceed & Book");

        } catch (TimeoutException te) {
            System.out.println("ℹ️ No extra popup appeared after Proceed & Book");
        } catch (Exception e) {
            System.out.println("⚠️ Error while handling post-proceed popup: " + e.getMessage());
        }
    }

    /* =========================================================
       UTIL METHODS
       ========================================================= */

    public void safeClick(By locator, String logMessage) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", element
            );
            element.click();
            System.out.println("✅ " + logMessage);
        } catch (Exception e) {
            try {
                WebElement element = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                System.out.println("⚡ JS clicked - " + logMessage);
            } catch (Exception ignored) {
                System.out.println("❌ Failed to click element: " + logMessage + " | " + e.getMessage());
            }
        }
    }

    public void waitAndScrollToElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element
        );
    }

    public boolean isElementPresent(By locator) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            boolean present = !driver.findElements(locator).isEmpty();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            return present;
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            return false;
        }
    }

    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
