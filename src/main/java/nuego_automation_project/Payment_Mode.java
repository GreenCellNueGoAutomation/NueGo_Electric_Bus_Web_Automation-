package nuego_automation_project;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Payment_Mode {

    WebDriver driver;

    // ✅ Constructor
    public Payment_Mode(WebDriver driver) {
        this.driver = driver;
    }

    // ✅ Step Locators
    By netBankingOption = By.id("1000404");                     // Step 1
    By axisBankOption = By.id("166");                           // Step 2
    By proceedToPayBtn = By.xpath("//article[normalize-space(.)='Proceed to Pay']");  // Step 3
    By txnDropdown = By.id("txnStateDropdownText");              // Step 4
    By chargedOption = By.cssSelector("li[data-value='CHARGED']"); // Step 5
    By submitBtn = By.id("submitButton");                        // Step 6

    // ✅ Step 1: Select Net Banking option
    public void selectNetBanking() {
        driver.findElement(netBankingOption).click();
        System.out.println("✅ Step 1: Clicked on Net Banking option");
    }

    // ✅ Step 2: Select Axis Bank
    public void selectAxisBank() {
        driver.findElement(axisBankOption).click();
        System.out.println("✅ Step 2: Selected Axis Bank");
    }

    // ✅ Step 3: Click Proceed to Pay
    public void clickProceedToPay() {
        driver.findElement(proceedToPayBtn).click();
        System.out.println("✅ Step 3: Clicked on Proceed to Pay button");
    }

    // ✅ Step 4: Click dropdown
    public void clickTxnDropdown() {
        driver.findElement(txnDropdown).click();
        System.out.println("✅ Step 4: Clicked on Transaction Status dropdown");
    }

    // ✅ Step 5: Select "CHARGED" option
    public void selectChargedOption() {
        driver.findElement(chargedOption).click();
        System.out.println("✅ Step 5: Selected 'CHARGED' option from dropdown");
    }

    // ✅ Step 6: Click Submit button
    public void clickSubmitButton() {
        driver.findElement(submitBtn).click();
        System.out.println("✅ Step 6: Clicked on Submit button");
    }

    // ✅ Optional: Execute all steps in sequence
    public void completePaymentFlow() throws InterruptedException {
        selectNetBanking();
        Thread.sleep(1000);

        selectAxisBank();
        Thread.sleep(1000);

        clickProceedToPay();
        Thread.sleep(2000);

        clickTxnDropdown();
        Thread.sleep(1000);

        selectChargedOption();
        Thread.sleep(1000);

        clickSubmitButton();
        System.out.println("🎉 Payment flow completed successfully!");
    }
}
