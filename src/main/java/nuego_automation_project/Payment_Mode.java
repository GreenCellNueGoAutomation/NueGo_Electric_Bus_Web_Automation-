package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Payment_Mode {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ---------- LOCATORS ----------

    public final By netBankingOption       = By.xpath("(//article[contains(text(),'NetBanking')])[2]");
    public final By axisBankOption         = By.xpath("//article[normalize-space()='Axis Bank']");
    public final By ProceedToPay           = By.xpath("//div[@id='103']//article[@role='none'][normalize-space()='Proceed to Pay']");

    // NEW LOCATORS
    public final By txnStateDropdownToggle = By.xpath("//button[@id='txnStateDropdownToggle']");
    public final By chargedOption          = By.xpath("//span[normalize-space()='CHARGED']");
    public final By submitButton           = By.xpath("//button[@id='submitButton']");

    // ---------- CONSTRUCTOR ----------
    public Payment_Mode(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
    }

    // =====================================================================
    //        WINDOW SWITCHING  (keep this because popup window is real)
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
    //                    CLICK METHOD (generic) - NO IFRAMES
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
    //        ACTION METHODS
    // =====================================================================

    public void clickNetBanking() {
        // In reschedule, still might open in new window → keep window switch
        switchToNewWindow();
        clickWithWait(netBankingOption, "NetBanking");
    }

    public void clickAxisBank() {
        clickWithWait(axisBankOption, "Axis Bank");
    }

    public void clickProceedToPay() {
        clickWithWait(ProceedToPay, "Proceed To Pay");
    }

    public void clickTxnStateDropdownToggle() {
        clickWithWait(txnStateDropdownToggle, "Txn State Dropdown Toggle");
    }

    public void clickChargedOption() {
        clickWithWait(chargedOption, "CHARGED option");
    }

    public void clickSubmitButton() {
        clickWithWait(submitButton, "Submit Button");
    }

    // =====================================================================
    //        COMBINED FLOW (NetBanking → Axis → Proceed → CHARGED → Submit)
    // =====================================================================
    public void completeNetBankingAxisFlow() throws InterruptedException {
        clickNetBanking();
        Thread.sleep(2000);

        clickAxisBank();
        Thread.sleep(2000);

        clickProceedToPay();
        Thread.sleep(2000);

        clickTxnStateDropdownToggle();
        Thread.sleep(2000);

        clickChargedOption();
        Thread.sleep(2000);

        clickSubmitButton();
        Thread.sleep(2000);
        
    }
}
