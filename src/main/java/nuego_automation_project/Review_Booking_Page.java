package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

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
            // 1) Co-passengers
            handleCoPassengerSelection();

            // 2) Discount popup
            handleDiscountPopup();
            scrollToReviewSection();

            // 3) Coupon
            if (isElementPresent(getCouponIconLocator())) {
                System.out.println("🔎 Coupon section found – applying coupon flow");

                double totalFareBeforeCoupon = 0.0;
                try {
                    totalFareBeforeCoupon = getTotalFareAmount();
                    System.out.println("💳 Captured Total Fare BEFORE coupon: " + totalFareBeforeCoupon);
                } catch (Exception e) {
                    System.out.println("⚠️ Could not capture total fare before coupon: " + e.getMessage());
                }

                openCouponModal();
                applyBestAvailableCoupon();
                waitForCouponSuccessMessageIfAny();

                if (totalFareBeforeCoupon > 0) {
                    logCouponDiscountAmount(totalFareBeforeCoupon);
                } else {
                    System.out.println("ℹ️ Skipping coupon discount calculation (no valid pre-coupon total).");
                }

            } else {
                System.out.println("ℹ️ Coupon icon not present on this flow – skipping coupon steps");
            }

            // 4) Assurance / Wallet / Miles
            scrollToAssuranceWalletSectionIfExists();

            if (isElementPresent(getAssuranceCheckboxLocator())) {
                clickAssuranceCheckbox();
            } else {
                System.out.println("ℹ️ Assurance checkbox not present – skipping Assurance step");
            }

            if (isElementPresent(getWalletApplyButtonLocator())) {
                applyWalletFixedOrLess();
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

            // 5) GST validation
            if (isElementPresent(getBaseFareLabelLocator()) &&
                    isElementPresent(getGstLabelLocator()) &&
                    isElementPresent(getTotalFareLabelLocator())) {
                validateGstAfterDiscounts();
            } else {
                System.out.println("ℹ️ GST / fare labels missing – skipping GST validation");
            }

            // 6) Proceed
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
        return By.xpath("//button[contains(text(),'Yes, Switch')]");
    }

    public void handleDiscountPopup() {
        try {
            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(6));
            WebElement popup = popupWait.until(
                    ExpectedConditions.visibilityOfElementLocated(getDiscountAlertPopupLocator())
            );

            if (popup.isDisplayed()) {
                System.out.println("💡 Discount Alert popup detected!");
                safeClick(getDiscountAlertNoThanksButtonLocator(), "Clicked 'yes switch' on Discount Alert popup");
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
                "//div[@class='coupon-list-component p-3']//div[@class='listing']/div[3]" +
                        "//p[contains(@class,'open-600w-16s-24h') " +
                        "and contains(@class,'teal-2-00A095-color') " +
                        "and contains(@class,'cursor-pointer') " +
                        "and normalize-space()='APPLY']"
        );
    }

    public By getCouponSuccessMessageLocator() {
        return By.xpath("//p[contains(text(),'Coupon Applied Successfully') or contains(text(),'applied successfully')]");
    }

    public By getCouponErrorMessageLocator() {
        return By.xpath("//*[contains(text(),'Invalid coupon') or contains(text(),'invalid coupon') or contains(text(),'not applicable')]");
    }

    public By getCouponCardsLocator() {
        return By.xpath("//div[@class='coupon-list-component p-3']//div[@class='listing']/div");
    }

    public By getApplyButtonInsideCouponLocator() {
        return By.xpath(".//p[contains(@class,'open-600w-16s-24h') " +
                "and contains(@class,'teal-2-00A095-color') " +
                "and contains(@class,'cursor-pointer') " +
                "and normalize-space()='APPLY']");
    }

    public boolean applyThirdCouponFromList() {
        try {
            if (!isElementPresent(getThirdCouponApplyLocator())) {
                System.out.println("ℹ️ 3rd coupon not present – cannot apply 3rd coupon.");
                return false;
            }

            safeClick(getThirdCouponApplyLocator(), "Clicked Apply on 3rd coupon");

            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(getCouponSuccessMessageLocator()));
                System.out.println("✅ 3rd coupon applied successfully.");
                return true;
            } catch (TimeoutException te) {
                System.out.println("⚠️ No success message after applying 3rd coupon, might be invalid.");
            }

            if (isElementPresent(getCouponErrorMessageLocator())) {
                System.out.println("❌ 3rd coupon appears to be invalid (error shown).");
                return false;
            }

            System.out.println("⚠️ No clear coupon success or error – treating 3rd coupon as invalid.");
            return false;

        } catch (Exception e) {
            System.out.println("⚠️ Error while applying 3rd coupon: " + e.getMessage());
            return false;
        }
    }

    public boolean applyAnyAvailableCouponIfThirdFails() {
        try {
            List<WebElement> coupons = driver.findElements(getCouponCardsLocator());
            if (coupons == null || coupons.isEmpty()) {
                System.out.println("ℹ️ No coupons found in the list.");
                return false;
            }

            System.out.println("🔍 Trying to find any valid coupon out of " + coupons.size() + " coupons.");

            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            int index = 0;
            for (WebElement coupon : coupons) {
                index++;
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block:'center'});", coupon
                    );
                    sleep(400);

                    WebElement applyBtn = coupon.findElement(getApplyButtonInsideCouponLocator());

                    try {
                        applyBtn.click();
                    } catch (Exception clickEx) {
                        System.out.println("⚠️ Normal click failed for coupon #" + index + ", trying JS click: " + clickEx.getMessage());
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
                    }

                    System.out.println("👉 Clicked APPLY on coupon #" + index);

                    try {
                        shortWait.until(ExpectedConditions.visibilityOfElementLocated(getCouponSuccessMessageLocator()));
                        System.out.println("✅ Coupon #" + index + " applied successfully.");
                        return true;
                    } catch (TimeoutException te) {
                        System.out.println("⚠️ No success message for coupon #" + index + ", checking for error...");
                    }

                    if (isElementPresent(getCouponErrorMessageLocator())) {
                        System.out.println("❌ Coupon #" + index + " seems invalid (error message displayed).");
                    } else {
                        System.out.println("⚠️ No explicit success or error for coupon #" + index + ". Trying next.");
                    }

                } catch (NoSuchElementException nse) {
                    System.out.println("⚠️ Apply button not found for coupon #" + index + ", skipping it.");
                } catch (Exception e) {
                    System.out.println("⚠️ Error while trying coupon #" + index + ": " + e.getMessage());
                }
            }

            System.out.println("❌ No valid coupon found after checking all.");
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error in applyAnyAvailableCouponIfThirdFails: " + e.getMessage());
            return false;
        }
    }

    public void applyBestAvailableCoupon() {
        boolean thirdApplied = applyThirdCouponFromList();
        if (!thirdApplied) {
            System.out.println("🔁 3rd coupon invalid or not applied – trying other coupons.");
            boolean anyApplied = applyAnyAvailableCouponIfThirdFails();
            if (!anyApplied) {
                System.out.println("ℹ️ Could not apply any valid coupon. Proceeding without coupon.");
            }
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

    public void logCouponDiscountAmount(double totalFareBeforeCoupon) {
        try {
            double totalFareAfterCoupon = getTotalFareAmount();
            System.out.println("💳 Total Fare before coupon: " + totalFareBeforeCoupon);
            System.out.println("💳 Total Fare after coupon:  " + totalFareAfterCoupon);

            double discount = totalFareBeforeCoupon - totalFareAfterCoupon;
            if (discount <= 0) {
                System.out.println("ℹ️ No positive discount detected from coupon (discount = " + discount + ")");
            } else {
                System.out.println("✅ Coupon discount applied: ₹ " + discount);
            }
        } catch (Exception e) {
            System.out.println("❌ Error while calculating coupon discount: " + e.getMessage());
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

    public By getWalletAmountInputLocator() {
        return By.xpath("//input[@type='number']");
    }

    public By getWalletBalanceTextLocator() {
        return By.xpath("//p[contains(text(),'Wallet Balance') or contains(text(),'wallet balance')]");
    }

    public double getWalletBalanceAmount() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement walletBalanceElement = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(getWalletBalanceTextLocator())
            );
            String text = walletBalanceElement.getText().trim();
            double balance = parseAmount(text);
            System.out.println("💰 Wallet Balance detected: " + balance);
            return balance;
        } catch (TimeoutException te) {
            System.out.println("ℹ️ Wallet balance text not visible on UI, assuming sufficient balance for rules. " + te.getMessage());
            return 999999.0;
        } catch (Exception e) {
            System.out.println("❌ Error reading wallet balance: " + e.getMessage());
            return 0.0;
        }
    }

    public void applyWalletFixedOrLess() {
        try {
            scrollToAssuranceWalletSectionIfExists();

            double totalFare = getTotalFareAmount();
            if (totalFare <= 0) {
                System.out.println("⚠️ Total fare is zero or invalid, skipping wallet apply.");
                return;
            }

            double walletBalance = getWalletBalanceAmount();
            if (walletBalance <= 0) {
                System.out.println("⚠️ No wallet balance available, skipping wallet apply.");
                return;
            }

            double amountToUse;
            if (totalFare >= 100) {
                amountToUse = Math.min(100.0, walletBalance);
            } else {
                double desired = totalFare - 1;
                if (desired <= 0) {
                    System.out.println("⚠️ Total fare too low, skipping wallet apply.");
                    return;
                }
                amountToUse = Math.min(desired, walletBalance);
            }

            int finalAmountInt = (int) Math.round(amountToUse);
            if (finalAmountInt <= 0) {
                System.out.println("⚠️ Computed wallet amount is zero or negative, skipping.");
                return;
            }
            String finalAmountStr = String.valueOf(finalAmountInt);

            System.out.println("💰 Wallet amount USED = ₹" + finalAmountStr +
                    " (Wallet balance: " + walletBalance +
                    ", Total fare: " + totalFare + ")");

            WebElement walletInput = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(getWalletAmountInputLocator())
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", walletInput
            );

            try {
                wait.until(ExpectedConditions.elementToBeClickable(walletInput));
                walletInput.click();
            } catch (Exception clickEx) {
                System.out.println("⚠️ Normal click on wallet input intercepted, trying JS click: " + clickEx.getMessage());
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", walletInput);
            }

            walletInput.clear();
            walletInput.sendKeys(finalAmountStr);

            try {
                WebElement applyBtn = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(getWalletApplyButtonLocator())
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", applyBtn
                );
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(applyBtn));
                    applyBtn.click();
                } catch (Exception ex) {
                    System.out.println("⚠️ Normal click on Wallet Apply intercepted, trying JS click: " + ex.getMessage());
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
                }

                sleep(800);
                System.out.println("🎯 Wallet applied successfully with amount: ₹" + finalAmountStr);
            } catch (Exception exOuter) {
                System.out.println("❌ Wallet Apply button not clickable / not found: " + exOuter.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to apply wallet amount: " + e.getMessage());
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
       CO-PASSENGER / GUEST SECTION  (ONLY GUEST 2 + GUEST 3 MANUAL)
       ========================================================= */

    // ================== GUEST 2 MANUAL LOCATORS ==================

    // Guest 2 Name input
    public By getGuest2NameInputLocator() {
        return By.xpath("//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']/div[@class='review-payment']/div[contains(@class,'light-fold position-relative py-5')]/div[@class='row']/div[@class='col-8']/div/div[@class='mt-4']/div[@class='personal-details-component']/div[@class='personal-detail-wrapper px-3']/div[2]/div[2]/div[1]/div[1]/input[1]");
    }

    // Guest 2 Age input
    public By getGuest2AgeInputLocator() {
        return By.xpath("//div[@class='personal-detail-wrapper px-3']//div[2]//div[2]//div[2]//div[1]//div[1]//div[1]//input[1]");
    }

    // Guest 2 Female option
    public By getGuest2FemaleOptionLocator() {
        return By.xpath("//div[@class='personal-detail-wrapper px-3']//div[2]//div[2]//div[2]//div[1]//div[2]//div[1]//div[2]//p[1]");
    }

    // ================== GUEST 3 MANUAL LOCATORS (ABSOLUTE FROM YOUR DOM) ==================

    // Guest 3 Name input
    public By getGuest3NameInputLocator() {
        return By.xpath(
                "//body/div[@id='root']/div[@class='booking-layout']/div[@class='auth-modal']" +
                        "/div[@class='review-payment']/div[contains(@class,'light-fold position-relative py-5')]" +
                        "/div[@class='row']/div[@class='col-8']/div/div[@class='mt-4']" +
                        "/div[@class='personal-details-component']/div[@class='personal-detail-wrapper px-3']" +
                        "/div[3]/div[2]/div[1]/div[1]/input[1]"
        );
    }

    // Guest 3 age input
    public By getGuest3AgeInputLocator() {
        return By.xpath("//div[3]//div[2]//div[2]//div[1]//div[1]//div[1]//input[1]");
    }

    // Guest 3 Male option
    public By getGuest3MaleOptionLocator() {
        return By.xpath("//div[3]//div[2]//div[2]//div[1]//div[2]//div[1]//div[2]//p[1]");
    }

    public void handleCoPassengerSelection() {
        try {
            System.out.println("👥 Starting Co-Passenger selection flow...");

            // Guest 2 + Guest 3 manual entries
            handleGuest2ManualEntry();
            handleGuest3ManualEntry();

            System.out.println("✅ Co-Passenger selection flow completed.");
        } catch (Exception e) {
            System.out.println("⚠️ Error in co-passenger flow (will continue rest of actions): " + e.getMessage());
        }
    }

    /* ---------- GUEST 2: NAME + AGE + FEMALE (MANUAL ENTRY) ---------- */
    public void handleGuest2ManualEntry() {
        try {
            System.out.println("👥 [Guest 2] Starting manual entry...");

            // 1) Name
            WebElement guest2Name = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(getGuest2NameInputLocator())
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", guest2Name
            );
            sleep(600);

            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", guest2Name); } catch (Exception ignore) {}
            try { guest2Name.clear(); } catch (Exception ignore) {}

            try {
                guest2Name.sendKeys("xyz");
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", guest2Name, "xyz");
            }
            System.out.println("✏️ Entered Guest 2 Name = xyz");
            sleep(600);

            // 2) Age
            try {
                WebElement guest2Age = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(getGuest2AgeInputLocator())
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", guest2Age
                );
                sleep(400);

                try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", guest2Age); } catch (Exception ignore) {}
                try { guest2Age.clear(); } catch (Exception ignore) {}

                try {
                    guest2Age.sendKeys("24");
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", guest2Age, "24");
                }
                System.out.println("✏️ Entered Guest 2 Age = 24");
                sleep(600);
            } catch (Exception e) {
                System.out.println("⚠️ Unable to set Age for Guest 2: " + e.getMessage());
            }

            // 3) Female
            try {
                WebElement femaleOption = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(getGuest2FemaleOptionLocator())
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", femaleOption
                );
                sleep(400);
                try {
                    femaleOption.click();
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", femaleOption);
                }
                System.out.println("🚺 Selected Gender = Female for Guest 2");
                sleep(400);
            } catch (Exception e) {
                System.out.println("⚠️ Unable to select Female option for Guest 2: " + e.getMessage());
            }

            System.out.println("✅ Guest 2 manual entry completed.");

        } catch (Exception e) {
            System.out.println("⚠️ Error in Guest 2 manual entry: " + e.getMessage());
        }
    }

    /* ---------- GUEST 3: NAME + AGE + MALE (MANUAL ENTRY – FIXED) ---------- */
    public void handleGuest3ManualEntry() {
        try {
            System.out.println("👥 [Guest 3] Starting manual entry...");

            // 1) NAME (ABC)
            WebElement guest3Name = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(getGuest3NameInputLocator())
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", guest3Name
            );
            sleep(600);

            try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", guest3Name); } catch (Exception ignore) {}
            try { guest3Name.clear(); } catch (Exception ignore) {}

            try {
                guest3Name.sendKeys("ABC");
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", guest3Name, "ABC");
            }
            System.out.println("✏️ Entered Guest 3 Name = ABC");
            sleep(600);

            // 2) AGE (25)
            try {
                WebElement ageElement = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(getGuest3AgeInputLocator())
                );

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", ageElement
                );
                sleep(400);

                try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ageElement); } catch (Exception ignore) {}
                try { ageElement.clear(); } catch (Exception ignore) {}

                try {
                    ageElement.sendKeys("25");
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", ageElement, "25");
                }
                System.out.println("✏️ Entered Guest 3 Age = 25");
                sleep(600);
            } catch (Exception e) {
                System.out.println("⚠️ Unable to set Age for Guest 3: " + e.getMessage());
            }

            // 3) MALE
            try {
                WebElement maleOption = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(getGuest3MaleOptionLocator())
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", maleOption
                );
                sleep(400);

                try {
                    maleOption.click();
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", maleOption);
                }

                System.out.println("🚹 Selected Gender = Male for Guest 3");
                sleep(400);
            } catch (Exception e) {
                System.out.println("⚠️ Unable to select Male option for Guest 3: " + e.getMessage());
            }

            System.out.println("✅ Guest 3 manual entry completed.");

        } catch (Exception e) {
            System.out.println("⚠️ Error in Guest 3 manual entry: " + e.getMessage());
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
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            boolean present = !driver.findElements(locator).isEmpty();
            return present;
        } catch (Exception e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        }
    }

    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
