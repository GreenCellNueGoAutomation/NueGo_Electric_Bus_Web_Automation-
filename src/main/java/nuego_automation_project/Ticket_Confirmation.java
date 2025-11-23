package nuego_automation_project;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Ticket_Confirmation {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private Actions actions;

    private static final int DEFAULT_WAIT_SECONDS = 20;
    private static final int SHORT_WAIT_SECONDS = 10;

    // global small delay between actions (to slow down script visually)
    private static final long ACTION_DELAY_MS = 700;

    private final String EXPECTED_URL = "https://greencell-nuego-web.web.app/confirmation";

    // ---------- LOCATORS ----------
    private final By dontAllowNotificationBtn = By.xpath("//button[@id='moe-dontallow_button']");
    private final By whatsappOption    = By.xpath("//p[normalize-space()='Whatsapp']");
    private final By smsOption         = By.xpath("//p[normalize-space()='SMS']");
    private final By fareIcon          = By.xpath("//img[@src='https://cdn.nuego.co.in/greencell/assets/images/CombinedShape.png']");
    private final By fareCloseIcon     = By.xpath("//div[contains(@class,'d-flex justify-content-between align-items-center margin-bottom-32')]//img[contains(@alt,'alt')]");
    private final By downloadTicketBtn = By.xpath("//p[contains(@class,'text-capitalize white-color opacity-60 mb-0 open-600w-12s-18h')][contains(text(),'Download')]");
    private final By eTicketOption     = By.xpath("//p[normalize-space()='E - ticket']");
    private final By copyLinkBtn       = By.xpath("//button[normalize-space()='Copy']");

    private final By successMessage = By.xpath(
            "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sent')]"
    );

    // ---------- CONSTRUCTOR ----------
    public Ticket_Confirmation(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_SECONDS));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_WAIT_SECONDS));
        this.actions = new Actions(driver);
    }

    // ---------- COMMON UTILITIES ----------
    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {}
    }

    private void delayAfterAction() {
        pause(ACTION_DELAY_MS);
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                element
        );
        // small pause for scroll animation / layout
        pause(500);
    }

    private void clickWithActions(WebElement element) {
        scrollIntoView(element);
        actions.moveToElement(element).click().perform();
        delayAfterAction();
    }

    private void waitAndClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        clickWithActions(element);
    }

    private WebElement waitForVisible(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        scrollIntoView(element);
        return element;
    }

    // ---------- VERIFICATIONS ----------
    public void verifyOnConfirmationPage() {
        wait.until(ExpectedConditions.urlToBe(EXPECTED_URL));
        delayAfterAction();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ---------- PAGE ACTIONS WITH WAITS ----------

    public void dismissNotificationIfPresent() {
        try {
            WebElement el = shortWait.until(ExpectedConditions.elementToBeClickable(dontAllowNotificationBtn));
            clickWithActions(el);
        } catch (Exception ignored) {
            // Notification not shown – safe to ignore
        }
    }

    public void clickWhatsappOnceAndVerifySuccess() {
        waitForVisible(whatsappOption);   // will scroll into view
        waitAndClick(whatsappOption);
        wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
        delayAfterAction();
    }

    public void clickSmsAndVerifySuccess() {
        waitForVisible(smsOption);
        waitAndClick(smsOption);
        wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
        delayAfterAction();
    }

    // ✅ open and close fare details, then scroll back to E-ticket/download area
    public void openAndCloseFareDetails() {
        // 1️⃣ Open fare details
        waitForVisible(fareIcon);
        waitAndClick(fareIcon);

        // 2️⃣ Close fare popup
        waitForVisible(fareCloseIcon);
        waitAndClick(fareCloseIcon);

        // 3️⃣ After closing, scroll down so E-ticket & Download are in view
        try {
            WebElement eTicketElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(eTicketOption)
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                    eTicketElement
            );
            pause(500);
        } catch (Exception e) {
            // Fallback if something goes wrong
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600);");
            pause(500);
        }
    }

    // 👉 E-Ticket (scrollIntoView + Actions)
    public void clickETicketOption() {
        waitForVisible(eTicketOption);
        waitAndClick(eTicketOption);
    }

    // 👉 Download Ticket (opens in new tab)
    public void clickDownloadAndVerifyTicketInNewTab() {
        // ensure Download button is in view and clickable
        waitForVisible(downloadTicketBtn);
        waitAndClick(downloadTicketBtn); // Actions + scrollIntoView

        String mainWindow = driver.getWindowHandle();

        // Wait for new window/tab
        WebDriverWait downloadWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        downloadWait.until(d -> d.getWindowHandles().size() > 1);

        String downloadWindow = null;
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindow)) {
                downloadWindow = handle;
                driver.switchTo().window(handle);
                break;
            }
        }

        if (downloadWindow == null) {
            throw new RuntimeException("Download window did not open");
        }

        pause(2000); // Give time for PDF / ticket to load
        String url = driver.getCurrentUrl(); // reserved for future assertions if needed

        driver.close();
        driver.switchTo().window(mainWindow);
        delayAfterAction();
    }

    public void clickCopyLink() {
        waitForVisible(copyLinkBtn);
        waitAndClick(copyLinkBtn);
    }

    // ---------- FULL FLOW ----------
    public void completeConfirmationActionsFlow() {
        // 1) WhatsApp twice
        clickWhatsappOnceAndVerifySuccess();
        clickWhatsappOnceAndVerifySuccess();

        // 2) SMS once
        clickSmsAndVerifySuccess();

        // 3) Fare details – open, close, then scroll to E-ticket zone
        openAndCloseFareDetails();

        // 4) E-Ticket
        clickETicketOption();

        // 5) Copy Link
        clickCopyLink();

        // 6) Download ticket in new tab, verify & return
        clickDownloadAndVerifyTicketInNewTab();
    }
}
