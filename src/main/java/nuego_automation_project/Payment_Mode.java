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

    // ---------- LOCATORS ----------
    private final By netBankingOption = By.xpath("(//article[contains(text(),'NetBanking')])[2]");
    private final By netBankingOption1 = By.xpath("(//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'netbanking')])[1]");
    private final By axisBankOption = By.xpath("//article[normalize-space()='Axis Bank']");
    private final By axisBankOption3 = By.xpath("//img[contains(@src,'jp_boxedlayout_tick.png')]");
    private final By axisBankOption1 = By.xpath("(//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'axis bank')])[1]");
  //  private final By payNowButton = By.xpath("//*[contains(text(),'Pay Now') or contains(text(),'Proceed') or contains(text(),'Continue')]");
    private final By ProceedToPay = By.xpath("(//article[normalize-space(text())='Proceed to Pay'])[1]");
    
    private final By ProceedToPay1 = By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[4]/div[1]/div[1]/div[2]");
    private final By dropdowntxt = By.xpath("//div[@id='txnStateDropdownText']");
    private final By clickCharged = By.xpath("//span[normalize-space(text())='CHARGED']");
    private final By sbumitbutton = By.xpath("//button[@id='submitButton']");

    // ---------- CONSTRUCTOR ----------
    public Payment_Mode(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        this.js = (JavascriptExecutor) driver;
    }

    // -------------------- INDIVIDUAL ACTION METHODS --------------------

    public void clickNetBanking() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(netBankingOption, "NetBanking");
    }

    public void clickAxisBank() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(axisBankOption, "Axis Bank");
    }

    public void clickAxisBankTick() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(axisBankOption3, "Axis Bank Tick Image");
    }

  /*  public void clickPayNow() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(payNowButton, "Pay Now / Proceed");
    }
*/
    public void clickProceedToPay() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(ProceedToPay, "Proceed To Pay");
       
    }
    
    public void clickProceedToclk() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(ProceedToPay1, "Proceed To Pay");
       
    }

    public void clickDropdownText() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(dropdowntxt, "Dropdown Text");
    }
    

    public void clickCharged() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(clickCharged, "CHARGED");
    }

    public void clickSubmitButton() throws InterruptedException {
        switchToPaymentIframeIfPresent();
        clickWithWait(sbumitbutton, "Submit Button");
    }

    // -------------------- MAIN METHODS --------------------

    public void selectNetBankingAndAxisBank() throws InterruptedException {
        clickNetBanking();
        Thread.sleep(1500);
        clickAxisBank();
    }

    public void completePaymentFlow() throws InterruptedException {
    //    clickPayNow();
        Thread.sleep(3000);
    }

    // -------------------- HELPER METHODS --------------------

    private void clickWithWait(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            element.click();
            System.out.println("✅ Clicked: " + elementName);
        } catch (Exception e) {
            System.out.println("⚠️ Normal click failed for " + elementName + ", trying JS click...");
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                js.executeScript("arguments[0].click();", element);
                System.out.println("✅ JS Clicked: " + elementName);
            } catch (Exception ex) {
                System.out.println("❌ Failed to click " + elementName + ": " + ex.getMessage());
            }
        }
    }

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
                } else {
                    driver.switchTo().defaultContent();
                }
            } catch (Exception ignored) {
                driver.switchTo().defaultContent();
            }
        }

        if (!frameFound) {
            System.out.println("ℹ️ No payment iframe detected — continuing in main DOM.");
        }
    }
}
