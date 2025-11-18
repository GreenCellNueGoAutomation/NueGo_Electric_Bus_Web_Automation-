package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class Payment_Mode {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ---------- LOCATORS (existing + new ones) ----------
    public final By netBankingOption       = By.xpath("(//article[contains(text(),'NetBanking')])[2]");
    public final By axisBankOption         = By.xpath("//article[normalize-space()='Axis Bank']");
    public final By ProceedToPay           = By.xpath("//div[@id='103']//article[@role='none'][normalize-space()='Proceed to Pay']");

    // 🔹 NEW LOCATORS
    public final By txnStateDropdownToggle = By.xpath("//button[@id='txnStateDropdownToggle']");
    public final By chargedOption          = By.xpath("//span[normalize-space()='CHARGED']");
    public final By submitButton           = By.xpath("//button[@id='submitButton']");

    // ---------- CONSTRUCTOR ----------
    public Payment_Mode(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        this.js = (JavascriptExecutor) driver;
    }

    // =====================================================================
    //        WINDOW SWITCHING
    // =====================================================================
    private void switchToNewWindow() {
        try {
            String parent = driver.getWindowHandle();

            for (String win : driver.getWindowHandles()) {
                if (!win.equals(parent)) {
                    driver.switchTo().window(win);
                    System.out.println("🪟 Switched to payment popup window");
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    // =====================================================================
    //               IFRAME SWITCHING
    // =====================================================================
    private void switchToPaymentIframeIfPresent() {
        driver.switchTo().defaultContent();

        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        System.out.println("🧩 Total iframes found: " + frames.size());

        boolean frameFound = false;

        for (int i = 0; i < frames.size(); i++) {
            try {
                driver.switchTo().frame(i);

                if (driver.findElements(netBankingOption).size() > 0 ||
                    driver.findElements(axisBankOption).size() > 0 ||
                    driver.findElements(ProceedToPay).size() > 0) {

                    System.out.println("✅ Switched to payment iframe #" + i);
                    frameFound = true;
                    break;
                }

                driver.switchTo().defaultContent();

            } catch (Exception ignored) {
                driver.switchTo().defaultContent();
            }
        }

        if (!frameFound)
            System.out.println("ℹ️ No payment iframe detected — staying in main DOM.");
    }

    // =====================================================================
    //                    CLICK METHOD (generic)
    // =====================================================================
    private void clickWithWait(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();

            System.out.println("✅ Clicked: " + elementName);

        } catch (Exception e) {
            System.out.println("⚠️ Normal click failed → Trying JS click for " + elementName);
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                js.executeScript("arguments[0].click();", element);
                System.out.println("✅ JS Clicked: " + elementName);
            } catch (Exception ex) {
                System.out.println("❌ Failed to click " + elementName + ": " + ex.getMessage());
            }
        }
    }

    // =====================================================================
    //        ACTION METHODS YOU NEED *NOW*
    // =====================================================================

    public void clickNetBanking() {
        switchToNewWindow();
        switchToPaymentIframeIfPresent();
        clickWithWait(netBankingOption, "NetBanking");
    }

    public void clickAxisBank() {
        switchToPaymentIframeIfPresent();
        clickWithWait(axisBankOption, "Axis Bank");
    }

    public void clickProceedToPay() {
        switchToPaymentIframeIfPresent();
        clickWithWait(ProceedToPay, "Proceed To Pay");
    }

    // 🔹 NEW ACTION METHODS

    public void clickTxnStateDropdownToggle() {
        switchToPaymentIframeIfPresent();
        clickWithWait(txnStateDropdownToggle, "Txn State Dropdown Toggle");
    }

    public void clickChargedOption() {
        switchToPaymentIframeIfPresent();
        clickWithWait(chargedOption, "CHARGED option");
    }

    public void clickSubmitButton() {
        switchToPaymentIframeIfPresent();
        clickWithWait(submitButton, "Submit Button");
    }

    // =====================================================================
    //        COMBINED FLOW (NetBanking → Axis → Proceed → CHARGED → Submit)
    // =====================================================================
    public void completeNetBankingAxisFlow() throws InterruptedException {
        clickNetBanking();
        Thread.sleep(3000);
        clickAxisBank();
        Thread.sleep(3000);
        clickProceedToPay();
        Thread.sleep(3000);

        // 🔹 Extra flow you asked for
        clickTxnStateDropdownToggle();
        Thread.sleep(3000);
        clickChargedOption();
        Thread.sleep(3000);
        clickSubmitButton();
    }
}
