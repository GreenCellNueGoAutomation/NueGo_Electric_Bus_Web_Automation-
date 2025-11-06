package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Payment_Mode {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Payment_Mode(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // ⏳ Increased to 60s for slow loads
    }

    // ✅ Locators
    private final By netBankingOption = By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[2]/div[3]/div[1]/div[1]/article[1]");
    private final By axisBankOption = By.xpath("//article[normalize-space()='Axis Bank']");
    private final By proceedToPayBtn = By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[4]/div[1]/div[1]/div[2]");
    private final By txnDropdown = By.xpath("//div[@id='txnStateDropdownText']");
    private final By chargedOption = By.xpath("//span[normalize-space()='CHARGED']");
    private final By submitBtn = By.xpath("//button[@id='submitButton']");

    // ✅ Wait for Payment Page Load
    public void waitForPaymentPage() {
        System.out.println("⏳ Waiting for Payment Mode page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(netBankingOption));
        System.out.println("✅ Payment Mode page loaded successfully.");
    }

    // ✅ Step 1: Click NetBanking
    public void selectNetBanking() {
        clickElement(netBankingOption, "Step 1: Clicked on Net Banking");
    }

    // ✅ Step 2: Select Axis Bank
    public void selectAxisBank() {
        clickElement(axisBankOption, "Step 2: Selected Axis Bank");
    }

    // ✅ Step 3: Click Proceed to Pay (with iframe + scroll fallback)
    public void clickProceedToPay() {
        System.out.println("⏳ Waiting for Proceed to Pay button...");
        try {
            // ✅ Check for iframe (common in payment pages)
            switchToPaymentIframeIfPresent();

            // ✅ Try clicking normally first
            clickElement(proceedToPayBtn, "Step 3: Clicked on Proceed to Pay button");

        } catch (Exception e) {
            System.out.println("⚠️ Proceed to Pay not clickable directly, trying JS click...");
            try {
                WebElement element = driver.findElement(proceedToPayBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                System.out.println("✅ JS clicked on Proceed to Pay button");
            } catch (Exception jsEx) {
                throw new AssertionError("❌ Failed to click Proceed to Pay: " + jsEx.getMessage());
            }
        }
    }

    // ✅ Step 4: Click Transaction Dropdown
    public void clickTxnDropdown() {
        clickElement(txnDropdown, "Step 4: Clicked on Transaction Status dropdown");
    }

    // ✅ Step 5: Select "CHARGED"
    public void selectChargedOption() {
        clickElement(chargedOption, "Step 5: Selected 'CHARGED' option");
    }

    // ✅ Step 6: Submit
    public void clickSubmitButton() {
        clickElement(submitBtn, "Step 6: Clicked on Submit button");
    }

    // ✅ Main Flow
    public void completePaymentFlow() {
        try {
            waitForPaymentPage();
            selectNetBanking();
            Thread.sleep(1500);
            selectAxisBank();
            Thread.sleep(1500);
            clickProceedToPay();
            Thread.sleep(2000);
            clickTxnDropdown();
            Thread.sleep(1000);
            selectChargedOption();
            Thread.sleep(1000);
            clickSubmitButton();
            System.out.println("🎉 Payment flow completed successfully!");
        } catch (Exception e) {
            throw new AssertionError("❌ Payment Flow failed: " + e.getMessage());
        }
    }

    // ✅ Helper to safely click any element
    private void clickElement(By locator, String logMessage) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
            System.out.println("✅ " + logMessage);
        } catch (TimeoutException te) {
            throw new AssertionError("Timeout: Element not clickable → " + locator);
        } catch (NoSuchElementException ne) {
            throw new AssertionError("Not Found: " + locator);
        }
    }

    // ✅ Detect and switch to iframe if payment form is inside one
    private void switchToPaymentIframeIfPresent() {
        try {
            int iframeCount = driver.findElements(By.tagName("iframe")).size();
            if (iframeCount > 0) {
                driver.switchTo().frame(0);
                System.out.println("🔄 Switched to payment iframe (index 0)");
            }
        } catch (Exception e) {
            System.out.println("ℹ️ No iframe found, continuing in main DOM");
        }
    }
}
